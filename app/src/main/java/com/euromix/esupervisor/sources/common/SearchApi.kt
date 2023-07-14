package com.euromix.esupervisor.sources.common

import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.sources.tasks.createTask.entities.OutletsForCreateTaskResponseEntity
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SearchApi {

    @GET("find_trading_agents")
    suspend fun findTradingAgents(@Query("search_string") searchString: String): List<ServerPair>

    @GET("partners")
    suspend fun findPartners(@Query("search_string") searchString: String): List<ServerPair>

    @GET("task_types")
    suspend fun findTasksType(): List<ServerPair>

//    @GET("outlets_inner_types")
//    suspend fun findOutletsInnerTypes(): List<ServerPair>

    @GET("search_selection_create_tasks")
    suspend fun searchSelectionsForCreateTasks(): List<List<ServerPair>>

    @GET("outlets_create_tasks")
    suspend fun findOutletsForCreateTask(@Header("request") request: String): List<OutletsForCreateTaskResponseEntity>

//    @GET("executors")
//    suspend fun findExecutors(@Query("search_string") searchString: String): List<ServerPair>
}