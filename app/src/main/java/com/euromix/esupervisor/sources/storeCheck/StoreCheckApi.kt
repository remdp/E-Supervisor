package com.euromix.esupervisor.sources.storeCheck

import com.euromix.esupervisor.sources.storeCheck.entities.StoreCheckLowerLevelResponseEntity
import com.euromix.esupervisor.sources.storeCheck.entities.StoreCheckRequestEntity
import com.euromix.esupervisor.sources.storeCheck.entities.StoreCheckTopLevelResponseEntity
import com.euromix.esupervisor.sources.storeCheck.entities.StoreCheckVisitResponseEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface StoreCheckApi {

    @GET("store_check/top_level")
    suspend fun getStoreCheckTopLevel(@Header("id") id: String): StoreCheckTopLevelResponseEntity

    @GET("store_check/lower_level")
    suspend fun getStoreCheckLowerLevel(@Header("id") id: String, @Header("top_level_id") topLevelId: String?): StoreCheckLowerLevelResponseEntity

    @POST("store_check/post")
    suspend fun storeCheck(@Header("id") id: String, @Body request: StoreCheckRequestEntity)

    @GET("store_checks")
    suspend fun getStoreChecksVisit(@Header("id") id: String): List<StoreCheckVisitResponseEntity>
}