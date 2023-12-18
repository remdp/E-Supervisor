package com.euromix.esupervisor.app.model.taskDetail

import com.euromix.esupervisor.app.utils.async.serverCallbackFlowFetcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskDetailRepository @Inject constructor(
    private val taskDetailSource: TaskDetailSource
) {
    fun getTask(id: String) = serverCallbackFlowFetcher { taskDetailSource.getTask(id) }
}