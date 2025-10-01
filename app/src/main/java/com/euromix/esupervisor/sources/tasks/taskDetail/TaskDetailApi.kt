package com.euromix.esupervisor.sources.tasks.taskDetail

import com.euromix.esupervisor.sources.tasks.createTask.entities.TaskChangeRequestEntity
import com.euromix.esupervisor.sources.tasks.taskDetail.entities.TaskDetailResponseEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

interface TaskDetailApi {

    @GET("task")
    suspend fun getTaskDetail(@Header("id") id: String): TaskDetailResponseEntity

    @PUT("task")
    suspend fun changeTask(@Body body: TaskChangeRequestEntity)

}