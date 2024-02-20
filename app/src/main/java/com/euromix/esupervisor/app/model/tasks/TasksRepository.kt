package com.euromix.esupervisor.app.model.tasks

import com.euromix.esupervisor.app.utils.async.serverCallbackFlowFetcher
import com.euromix.esupervisor.app.utils.toDate
import com.euromix.esupervisor.sources.tasks.createTask.entities.TasksCreateRequestEntity
import com.euromix.esupervisor.sources.tasks.list.entities.TasksRequestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasksRepository @Inject constructor(
    private val tasksSource: TasksSource
) {
    fun createTasks(body: TasksCreateRequestEntity) =
        serverCallbackFlowFetcher { tasksSource.createTasks(body) }

    fun getTasks(request: TasksRequestEntity) =
        serverCallbackFlowFetcher { tasksSource.getTasks(request) }

    fun getNextVisitDate(id: String) =
        serverCallbackFlowFetcher { tasksSource.getDateNextVisit(id).toDate() }
}