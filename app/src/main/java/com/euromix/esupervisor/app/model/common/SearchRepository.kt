package com.euromix.esupervisor.app.model.common

import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.utils.async.LazyFlowSubject
import com.euromix.esupervisor.sources.tasks.createTask.entities.OutletsForCreateTaskRequestEntity
import com.euromix.esupervisor.sources.tasks.createTask.entities.OutletsForCreateTaskResponseEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val searchSource: SearchSource
) {

    private val tradingAgentsFlowSubject = LazyFlowSubject<String, List<ServerPair>> {
        searchSource.findTradingAgents(it)
    }

    private val partnersFlowSubject = LazyFlowSubject<String, List<ServerPair>> {
        searchSource.findPartners(it)
    }

    private val tasksTypeFlowSubject = LazyFlowSubject<String, List<ServerPair>> {
        searchSource.findTasksType()
    }

    private val executorsFlowSubject = LazyFlowSubject<String, List<ServerPair>> {
        searchSource.findExecutors(it)
    }

    private val searchSelectionsForCreateTasksFlowSubject = LazyFlowSubject<String, List<List<ServerPair>>> {
        searchSource.searchSelectionsForCreateTasks()
    }

    private val outletsForCreateTasksFlowSubject =
        LazyFlowSubject<OutletsForCreateTaskRequestEntity, List<OutletsForCreateTaskResponseEntity>> {
            searchSource.findOutletsForCreateTask(it)
        }

    fun findTradingAgents(stringSearch: String): Flow<Result<List<ServerPair>>> {
        return tradingAgentsFlowSubject.listen(stringSearch)
    }

    fun findPartners(stringSearch: String): Flow<Result<List<ServerPair>>> {
        return partnersFlowSubject.listen(stringSearch)
    }

    fun findTasksType(): Flow<Result<List<ServerPair>>> {
        return tasksTypeFlowSubject.listen("")
    }

    fun findExecutors(stringSearch: String): Flow<Result<List<ServerPair>>> {
        return executorsFlowSubject.listen(stringSearch)
    }

    fun searchSelectionsForCreateTasks(): Flow<Result<List<List<ServerPair>>>> {
        return searchSelectionsForCreateTasksFlowSubject.listen("")
    }

    fun findOutletsForCreateTask(request: OutletsForCreateTaskRequestEntity): Flow<Result<List<OutletsForCreateTaskResponseEntity>>> {
        return outletsForCreateTasksFlowSubject.listen(request)
    }

}