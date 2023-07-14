package com.euromix.esupervisor.app.model.taskDetail

import com.euromix.esupervisor.app.model.taskDetail.entities.TaskDetail

interface TaskDetailSource {

   suspend fun getTask(id: String): TaskDetail

}