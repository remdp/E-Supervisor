package com.euromix.esupervisor.app.model.routes

import com.euromix.esupervisor.app.model.routes.entities.MapPoint
import com.euromix.esupervisor.app.model.routes.entities.OutletData
import com.euromix.esupervisor.app.model.routes.entities.VisitsStatisticDetailData
import com.euromix.esupervisor.sources.routes.entities.MapPointsRequestEntity
import com.euromix.esupervisor.sources.routes.entities.OutletDataRequestEntity
import com.euromix.esupervisor.sources.routes.entities.RoutesStatisticDetailRequestEntity
import com.euromix.esupervisor.sources.routes.entities.RoutesStatisticRequestEntity

interface RoutesSource {
    suspend fun getRoutes(request: MapPointsRequestEntity): List<MapPoint>

    suspend fun getOutletData(request: OutletDataRequestEntity): OutletData

    suspend fun getVisitsStatisticData(request: RoutesStatisticRequestEntity): List<Any>

    suspend fun getVisitsStatisticDetailData(request: RoutesStatisticDetailRequestEntity): VisitsStatisticDetailData
}