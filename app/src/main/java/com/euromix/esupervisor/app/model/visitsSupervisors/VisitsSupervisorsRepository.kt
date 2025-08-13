package com.euromix.esupervisor.app.model.visitsSupervisors

import com.euromix.esupervisor.app.utils.async.serverCallbackFlowFetcher
import com.euromix.esupervisor.sources.visitsSupervisors.entities.CheckInRequestEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.CheckOutRequestEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.RepeatStoreCheckRequestEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.VisitsSupervisorsRequestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitsSupervisorsRepository @Inject constructor(private val visitsSupervisorsSource: VisitsSupervisorsSource) {
    fun getVisitsSupervisors(request: VisitsSupervisorsRequestEntity) =
        serverCallbackFlowFetcher { visitsSupervisorsSource.getVisitsSupervisors(request) }

    fun getVisitSupervisorDetail(id: String) =
        serverCallbackFlowFetcher { visitsSupervisorsSource.getVisitSupervisorDetail(id) }

    fun checkInPost(id: String, request: CheckInRequestEntity) =
        serverCallbackFlowFetcher { visitsSupervisorsSource.checkInPost(id, request) }

    fun checkInGet(id: String) =
        serverCallbackFlowFetcher { visitsSupervisorsSource.checkInGet(id) }

    fun checkOutPost(id: String, request: CheckOutRequestEntity) =
        serverCallbackFlowFetcher { visitsSupervisorsSource.checkOutPost(id, request) }

    fun checkOutGet(id: String) =
        serverCallbackFlowFetcher { visitsSupervisorsSource.checkOutGet(id) }

    fun createRepeatStoreCheck(request: RepeatStoreCheckRequestEntity) =
        serverCallbackFlowFetcher { visitsSupervisorsSource.createRepeatStoreCheck(request)}
}