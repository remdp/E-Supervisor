package com.euromix.esupervisor.app.model.tasks

import com.euromix.esupervisor.app.model.tasks.entities.Outlet
import com.euromix.esupervisor.app.model.tasks.entities.Task
//import com.euromix.esupervisor.sources.tasks.createTask.entities.TaskCreateDislikeRequestEntity
import com.euromix.esupervisor.sources.tasks.createTask.entities.TasksCreateRequestEntity
import com.euromix.esupervisor.sources.tasks.list.entities.TasksRequestEntity

interface TasksSource {

    suspend fun getTasks(request: TasksRequestEntity): List<Task>

    suspend fun createTasks(body: TasksCreateRequestEntity): String

    suspend fun getDateNextVisit(id: String): String

}