package com.euromix.esupervisor.app.model.visits

import com.euromix.esupervisor.app.utils.async.serverCallbackFlowFetcher
import com.euromix.esupervisor.sources.visits.entities.VisitsChangeTypeRequestEntity
import com.euromix.esupervisor.sources.visits.entities.VisitsRequestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitsRepository @Inject constructor(private val visitsSource: VisitsSource) {

    fun getVisits(request: VisitsRequestEntity) =
        serverCallbackFlowFetcher { visitsSource.getVisits(request) }

    fun getChangeTypeVisitReasons() =
        serverCallbackFlowFetcher { visitsSource.getChangeTypeVisitReasons() }

    fun changeVisitsType(visitsChangeType: VisitsChangeTypeRequestEntity) =
        serverCallbackFlowFetcher { visitsSource.changeVisitsType(visitsChangeType) }
}