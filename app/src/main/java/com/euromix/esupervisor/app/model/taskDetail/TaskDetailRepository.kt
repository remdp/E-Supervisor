package com.euromix.esupervisor.app.model.taskDetail

import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.taskDetail.entities.TaskDetail
import com.euromix.esupervisor.app.utils.async.LazyFlowSubject
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskDetailRepository @Inject constructor(
    private val taskDetailSource: TaskDetailSource
) {

    private val taskDetailLazyFlowSubject = LazyFlowSubject<String, TaskDetail> {
        taskDetailSource.getTask(it)
    }

    fun getTask(id: String): Flow<Result<TaskDetail>> {
        return taskDetailLazyFlowSubject.listen(id)
    }

}