package com.euromix.esupervisor.app.model.tasks

import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.tasks.entities.Task
import com.euromix.esupervisor.app.utils.async.LazyFlowSubject
import com.euromix.esupervisor.sources.tasks.createTask.entities.TasksCreateRequestEntity
import com.euromix.esupervisor.sources.tasks.list.entities.TasksRequestEntity
import kotlinx.coroutines.flow.Flow
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

//    private val tasksOutletsLazyFlowSubject = LazyFlowSubject<String, List<Outlet>> {
//        tasksSource.getOutlets(it)
//    }

//    private val taskTypesLazyFlowSubject = LazyFlowSubject<Boolean, List<ServerPair>> {
//        tasksSource.getTaskTypes()
//    }

    fun getTasks(request: TasksRequestEntity): Flow<Result<List<Task>>> {
        return tasksLazyFlowSubject.listen(request)
    }

    fun createTasks(body: TasksCreateRequestEntity):Flow<Result<String>>{
        return createTasksLazyFlowSubject.listen(body)
    }

//    fun getTasksOutlets(id: String): Flow<Result<List<Outlet>>> {
//        return tasksOutletsLazyFlowSubject.listen(id)
//    }

//    fun getTaskTypes(): Flow<Result<List<ServerPair>>>{
//        return taskTypesLazyFlowSubject.listen(true)
//    }
}