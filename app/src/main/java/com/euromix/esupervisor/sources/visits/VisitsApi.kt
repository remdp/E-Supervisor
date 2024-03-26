package com.euromix.esupervisor.sources.visits

import com.euromix.esupervisor.sources.visits.entities.ChangeVisitTypeReasonResponseEntity
import com.euromix.esupervisor.sources.visits.entities.VisitsChangeTypeRequestEntity
import com.euromix.esupervisor.sources.visits.entities.VisitsResponseEntity
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface VisitsApi {
    @GET("visits")
    suspend fun getVisits(
        @Header("request") request: String? = null
    ): List<VisitsResponseEntity>

    @GET("change_type_visit_reasons")
    suspend fun getChangeTypeVisitReasons(): List<ChangeVisitTypeReasonResponseEntity>

    @POST("visits")
    suspend fun changeVisitsType(
        @Body body: VisitsChangeTypeRequestEntity
    ): ResponseBody
}