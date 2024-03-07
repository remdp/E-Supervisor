package com.euromix.esupervisor.sources.visits

import com.euromix.esupervisor.sources.visits.entities.VisitsResponseEntity
import retrofit2.http.GET
import retrofit2.http.Header

interface VisitsApi {

    @GET("visits")
    suspend fun getVisits(
        @Header("request") request: String? = null
    ): List<VisitsResponseEntity>
}