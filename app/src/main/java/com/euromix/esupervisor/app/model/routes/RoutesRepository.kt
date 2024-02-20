package com.euromix.esupervisor.app.model.routes

import com.euromix.esupervisor.app.utils.async.serverCallbackFlowFetcher
import com.euromix.esupervisor.sources.routes.entities.MapPointsRequestEntity
import com.euromix.esupervisor.sources.routes.entities.OutletDataRequestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutesRepository @Inject constructor(private val routesSource: RoutesSource) {
    fun getMapPoints(request: MapPointsRequestEntity) =
        serverCallbackFlowFetcher { routesSource.getRoutes(request) }

    fun getOutletData(request: OutletDataRequestEntity) =
        serverCallbackFlowFetcher { routesSource.getOutletData(request) }
}