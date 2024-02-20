package com.euromix.esupervisor.app.model.common

import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.sources.routes.entities.TradingAgentsAndTeams
import com.euromix.esupervisor.sources.tasks.createTask.entities.OutletsForCreateTaskRequestEntity
import com.euromix.esupervisor.sources.tasks.createTask.entities.OutletsForCreateTaskResponseEntity

interface SearchSource {
    suspend fun findTradingAgents(searchString: String): List<ServerPair>

    suspend fun findPartners(searchString: String): List<ServerPair>

    suspend fun findTasksType(): List<ServerPair>

    suspend fun findExecutors(searchString: String): List<ServerPair>

    suspend fun searchSelectionsForCreateTasks(): List<List<ServerPair>>

    suspend fun findOutletsForCreateTask(request: OutletsForCreateTaskRequestEntity): List<OutletsForCreateTaskResponseEntity>

    suspend fun findTradingTeams(): List<ServerPair>

    suspend fun findTradingAgentsAndTeams(searchString: String): List<TradingAgentsAndTeams>
}