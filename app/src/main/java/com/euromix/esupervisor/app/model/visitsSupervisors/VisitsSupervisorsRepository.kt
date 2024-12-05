package com.euromix.esupervisor.app.model.visitsSupervisors

import com.euromix.esupervisor.app.utils.async.serverCallbackFlowFetcher
import com.euromix.esupervisor.sources.visitsSupervisors.entities.CheckInRequestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitsSupervisorsRepository @Inject constructor(private val visitsSupervisorsSource: VisitsSupervisorsSource) {
    fun getVisitsSupervisors() =
        serverCallbackFlowFetcher { visitsSupervisorsSource.getVisitsSupervisors() }

    fun getVisitSupervisorDetail(id: String) =
        serverCallbackFlowFetcher { visitsSupervisorsSource.getVisitSupervisorDetail(id) }

    fun checkIn(id: String, request: CheckInRequestEntity) =
        serverCallbackFlowFetcher { visitsSupervisorsSource.checkIn(id, request) }
}