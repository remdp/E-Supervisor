package com.euromix.esupervisor.sources.routes

import com.euromix.esupervisor.sources.routes.entities.MapPointsResponseEntity
import com.euromix.esupervisor.sources.routes.entities.OutletDataResponseEntity
import retrofit2.http.GET
import retrofit2.http.Header

interface RoutesApi {

    @GET("routes")
    suspend fun getRoutes(@Header("request") request: String): List<MapPointsResponseEntity>

    @GET("outlet_data")
    suspend fun getOutletData(@Header("request") request: String): OutletDataResponseEntity
}