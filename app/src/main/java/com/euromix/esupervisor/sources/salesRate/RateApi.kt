package com.euromix.esupervisor.sources.salesRate

import com.euromix.esupervisor.sources.salesRate.entities.*
import retrofit2.http.Body
import retrofit2.http.POST

interface RateApi {

    @POST("rate")
    suspend fun getRate(@Body body: RateRequestEntity): RateResponseEntity
}