package com.euromix.esupervisor.sources.tasks.taskDetail

import com.euromix.esupervisor.sources.tasks.taskDetail.entities.TaskDetailResponseEntity
import retrofit2.http.GET
import retrofit2.http.Header

interface TaskDetailApi {

    @GET("task")
    suspend fun getTaskDetail(@Header("id") id: String): TaskDetailResponseEntity

}