package com.euromix.esupervisor.sources.visitsSupervisors

import com.euromix.esupervisor.sources.visitsSupervisors.entities.CheckInRequestEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.CheckInResponseEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.CheckOutRequestEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.CheckOutResponseEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.RepeatStoreCheckRequestEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.TransferStoreCheckRequestEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.VisitSupervisorDetailResponseEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.VisitsSupervisorResponseEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface VisitsSupervisorsApi {
    @GET("visits_supervisor")
    suspend fun getVisitsSupervisor(@Header("request") request: String? = null): List<VisitsSupervisorResponseEntity>

    @GET("visit_supervisor")
    suspend fun getVisitSupervisorDetail(@Header("id") id: String): VisitSupervisorDetailResponseEntity

    @POST("check_in")
    suspend fun checkInPost(@Header("id") id: String, @Body request: CheckInRequestEntity): CheckInResponseEntity

    @GET("check_in")
    suspend fun checkInGet(@Header("id") id: String): CheckInResponseEntity

    @POST("check_out")
    suspend fun checkOutPost(@Header("id") id: String, @Body request: CheckOutRequestEntity): CheckOutResponseEntity

    @GET("check_out")
    suspend fun checkOutGet(@Header("id") id: String): CheckOutResponseEntity

    @POST("store_checks_repeat")
    suspend fun createRepeatStoreCheck(@Body request: RepeatStoreCheckRequestEntity): String

    @POST("store_checks_transfer")
    suspend fun transferStoreCheck(@Body request: TransferStoreCheckRequestEntity): String

}