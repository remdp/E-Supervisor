package com.euromix.esupervisor.app.model.storeCheck

import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckLowerLevel
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckTopLevel
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckVisit
import com.euromix.esupervisor.sources.storeCheck.entities.StoreCheckRequestEntity

interface StoreCheckSource {
    suspend fun getStoreCheckTopLevel(id: String): StoreCheckTopLevel
    suspend fun getStoreCheckLowerLevel(id: String, topLevelId: String?): StoreCheckLowerLevel
    suspend fun postStoreCheck(id: String, request: StoreCheckRequestEntity)
    suspend fun getStoreChecksVisit(id: String): List<StoreCheckVisit>
}