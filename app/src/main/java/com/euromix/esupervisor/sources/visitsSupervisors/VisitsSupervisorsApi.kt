package com.euromix.esupervisor.sources.visitsSupervisors

import com.euromix.esupervisor.sources.visitsSupervisors.entities.CheckInRequestEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.VisitSupervisorDetailResponseEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.VisitsSupervisorResponseEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface VisitsSupervisorsApi {
    @GET("visits_supervisor")
    suspend fun getVisitsSupervisor(): List<VisitsSupervisorResponseEntity>

    @GET("visit_supervisor")
    suspend fun getVisitSupervisorDetail(@Header("id") id: String): VisitSupervisorDetailResponseEntity

    @POST("check_in")
    suspend fun checkIn(@Header("id") id: String, @Body request: CheckInRequestEntity)
}