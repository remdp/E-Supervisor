package com.euromix.esupervisor.sources.tasks.list

import com.euromix.esupervisor.sources.tasks.createTask.entities.TasksCreateRequestEntity
import com.euromix.esupervisor.sources.tasks.list.entities.OutletsResponseEntity
import com.euromix.esupervisor.sources.tasks.list.entities.TasksResponseEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TasksApi {

    @GET("tasks")
    suspend fun getTasks(@Header("request") request: String): List<TasksResponseEntity>

    @POST("tasks_create")
    suspend fun createTasks(@Body body: TasksCreateRequestEntity): String

    @GET("date_next_visit")
    suspend fun getDateNextVisit(@Header("id") id: String): String

}