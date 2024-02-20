package com.euromix.esupervisor.app.model.routes

import com.euromix.esupervisor.app.model.routes.entities.MapPoint
import com.euromix.esupervisor.app.model.routes.entities.OutletData
import com.euromix.esupervisor.sources.routes.entities.MapPointsRequestEntity
import com.euromix.esupervisor.sources.routes.entities.OutletDataRequestEntity

interface RoutesSource {
    suspend fun getRoutes(request: MapPointsRequestEntity): List<MapPoint>

    suspend fun getOutletData(request: OutletDataRequestEntity): OutletData
}