package com.euromix.esupervisor.app.model.storeCheck

import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckLowerLevel
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckTopLevel
import com.euromix.esupervisor.app.model.visitsSupervisors.VisitsSupervisorsSource
import com.euromix.esupervisor.app.utils.async.serverCallbackFlowFetcher
import com.euromix.esupervisor.sources.storeCheck.entities.StoreCheckRequestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreCheckRepository @Inject constructor(private val storeCheckSource: StoreCheckSource) {

    fun getStoreCheckTopLevel(id: String) =
        serverCallbackFlowFetcher { storeCheckSource.getStoreCheckTopLevel(id) }

    fun getStoreCheckLowerLevel(id: String, topLevelId: String?) =
        serverCallbackFlowFetcher { storeCheckSource.getStoreCheckLowerLevel(id, topLevelId) }

    fun postStoreCheck(id: String, request: StoreCheckRequestEntity) =
        serverCallbackFlowFetcher { storeCheckSource.postStoreCheck(id, request) }

    fun getStoreChecksVisit(id: String) =
        serverCallbackFlowFetcher { storeCheckSource.getStoreChecksVisit(id) }
}