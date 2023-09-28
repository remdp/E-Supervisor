package com.euromix.esupervisor.app.model.tasks

import com.euromix.esupervisor.App
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.tasks.entities.Task
import com.euromix.esupervisor.app.utils.async.LazyFlowSubject
//import com.euromix.esupervisor.sources.tasks.createTask.entities.TaskCreateDislikeRequestEntity
import com.euromix.esupervisor.sources.tasks.createTask.entities.TasksCreateRequestEntity
import com.euromix.esupervisor.sources.tasks.list.entities.TasksRequestEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasksRepository @Inject constructor(
    private val tasksSource: TasksSource
) {

    private val tasksLazyFlowSubject = LazyFlowSubject<TasksRequestEntity, List<Task>> {
        tasksSource.getTasks(it)
    }

    private val createTasksLazyFlowSubject = LazyFlowSubject<TasksCreateRequestEntity, String> {
        tasksSource.createTasks(it)
    }

    fun getTasks(request: TasksRequestEntity): Flow<Result<List<Task>>> {
        return tasksLazyFlowSubject.listen(request)
    }

    fun createTasks(body: TasksCreateRequestEntity): Flow<Result<String>> {
        return createTasksLazyFlowSubject.listen(body)
    }

    fun getNextVisitDate(id: String): Flow<Result<Date>> {
        return callbackFlow {

            try {
                trySend(Pending())
                val res = tasksSource.getDateNextVisit(id)
                trySend(Success(App.stringToDate(res)))
            } catch (e: Exception) {
                trySend(Error(e))
            }

            awaitClose { }

        }
    }
}