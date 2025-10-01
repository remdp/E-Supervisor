package com.euromix.esupervisor.app.model.taskDetail

import com.euromix.esupervisor.app.model.taskDetail.entities.TaskDetail
import com.euromix.esupervisor.sources.tasks.createTask.entities.TaskChangeRequestEntity

interface TaskDetailSource {

   suspend fun getTask(id: String): TaskDetail

   suspend fun changeTask(body: TaskChangeRequestEntity)

}