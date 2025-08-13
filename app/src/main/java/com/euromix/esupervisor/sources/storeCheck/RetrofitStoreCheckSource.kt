package com.euromix.esupervisor.sources.storeCheck

import com.euromix.esupervisor.app.model.storeCheck.StoreCheckSource
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckLowerLevel
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckTopLevel
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckVisit
import com.euromix.esupervisor.sources.base.BaseRetrofitSource
import com.euromix.esupervisor.sources.base.RetrofitConfig
import com.euromix.esupervisor.sources.storeCheck.entities.StoreCheckRequestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitStoreCheckSource @Inject constructor(config: RetrofitConfig) :
    BaseRetrofitSource(config), StoreCheckSource {

    private val storeCheckApi = retrofit.create(StoreCheckApi::class.java)

    override suspend fun getStoreCheckTopLevel(id: String): StoreCheckTopLevel {
        return wrapRetrofitException {
            storeCheckApi.getStoreCheckTopLevel(id).toStoreCheckTopLevel()
        }
    }

    override suspend fun getStoreCheckLowerLevel(
        id: String,
        topLevelId: String?
    ): StoreCheckLowerLevel {
        return wrapRetrofitException {
            storeCheckApi.getStoreCheckLowerLevel(id, topLevelId).toStoreCheckLowerLevel()
        }
    }

    override suspend fun postStoreCheck(id: String, request: StoreCheckRequestEntity) {
        return wrapRetrofitException {
            storeCheckApi.storeCheck(id, request)
        }
    }

    override suspend fun getStoreChecksVisit(id: String) =
        wrapRetrofitException {
            storeCheckApi.getStoreChecksVisit(id).map { it.toStoreCheckVisit() }
        }
}