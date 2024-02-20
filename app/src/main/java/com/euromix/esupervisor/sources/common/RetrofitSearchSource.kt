package com.euromix.esupervisor.sources.common

import com.euromix.esupervisor.app.model.common.SearchSource
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.sources.base.BaseRetrofitSource
import com.euromix.esupervisor.sources.base.RetrofitConfig
import com.euromix.esupervisor.sources.routes.entities.TradingAgentsAndTeams
import com.euromix.esupervisor.sources.tasks.createTask.entities.OutletsForCreateTaskRequestEntity
import com.euromix.esupervisor.sources.tasks.createTask.entities.OutletsForCreateTaskResponseEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitSearchSource @Inject constructor(
    private val config: RetrofitConfig
) : BaseRetrofitSource(config),
    SearchSource {

    private val searchApi = retrofit.create(SearchApi::class.java)

    override suspend fun findTradingAgents(searchString: String): List<ServerPair> {
        return wrapRetrofitException {
            searchApi.findTradingAgents(searchString)
        }
    }

    override suspend fun findPartners(searchString: String): List<ServerPair> {
        return wrapRetrofitException {
            searchApi.findPartners(searchString)
        }
    }

    override suspend fun findTasksType(): List<ServerPair> {
        return wrapRetrofitException {
            searchApi.findTasksType()
        }
    }

    override suspend fun findExecutors(searchString: String): List<ServerPair> {
        return wrapRetrofitException {
            searchApi.findTradingAgents(searchString)
        }
    }

    override suspend fun searchSelectionsForCreateTasks(): List<List<ServerPair>> {
        return wrapRetrofitException {
            searchApi.searchSelectionsForCreateTasks()
        }
    }

    override suspend fun findOutletsForCreateTask(request: OutletsForCreateTaskRequestEntity): List<OutletsForCreateTaskResponseEntity> {
        return wrapRetrofitException {
            val jsonAdapter = config.moshi.adapter(OutletsForCreateTaskRequestEntity::class.java)
            searchApi.findOutletsForCreateTask(jsonAdapter.toJson(request))
        }
    }

    override suspend fun findTradingTeams(): List<ServerPair> {
        return wrapRetrofitException {
            searchApi.findTradingTeams()
        }
    }

    override suspend fun findTradingAgentsAndTeams(searchString: String): List<TradingAgentsAndTeams> {
        return wrapRetrofitException {
            searchApi.findTradingAgentsAndTeams(searchString).map { it.toTradingAgentsAndTeams() }
        }
    }
}