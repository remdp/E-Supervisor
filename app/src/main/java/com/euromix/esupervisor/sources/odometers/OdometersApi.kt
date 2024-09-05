package com.euromix.esupervisor.sources.odometers

import com.euromix.esupervisor.sources.odometers.entities.TodayOdometersReadingRequestEntity
import com.euromix.esupervisor.sources.odometers.entities.OdometersReadingResponseEntity
import com.euromix.esupervisor.sources.odometers.entities.TodayOdometersReadingResponseEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface OdometersApi {
    @GET("odometers_reading_today")
    suspend fun getTodayOdometersReading(): TodayOdometersReadingResponseEntity

    @POST("odometers_reading_today")
    suspend fun sendTodayOdometersReading(@Body request: TodayOdometersReadingRequestEntity): TodayOdometersReadingResponseEntity

    @GET("odometers_reading_list")
    suspend fun getOdometersReadingList(@Header("request") request: String? = null): List<OdometersReadingResponseEntity>
}
