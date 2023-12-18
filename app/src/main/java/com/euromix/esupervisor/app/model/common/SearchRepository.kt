package com.euromix.esupervisor.app.model.common

import com.euromix.esupervisor.app.utils.async.serverCallbackFlowFetcher
import com.euromix.esupervisor.sources.tasks.createTask.entities.OutletsForCreateTaskRequestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val searchSource: SearchSource
) {
    fun findTradingAgents(stringSearch: String) =
        serverCallbackFlowFetcher { searchSource.findTradingAgents(stringSearch) }

    fun findPartners(stringSearch: String) =
        serverCallbackFlowFetcher { searchSource.findPartners(stringSearch) }

    fun findTasksType() = serverCallbackFlowFetcher { searchSource.findTasksType() }

    fun findExecutors(stringSearch: String) =
        serverCallbackFlowFetcher { searchSource.findExecutors(stringSearch) }

    fun searchSelectionsForCreateTasks() =
        serverCallbackFlowFetcher { searchSource.searchSelectionsForCreateTasks() }

    fun findOutletsForCreateTask(request: OutletsForCreateTaskRequestEntity) =
        serverCallbackFlowFetcher { searchSource.findOutletsForCreateTask(request) }
}