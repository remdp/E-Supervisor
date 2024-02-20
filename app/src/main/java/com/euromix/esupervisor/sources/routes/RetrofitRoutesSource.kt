package com.euromix.esupervisor.sources.routes

import com.euromix.esupervisor.app.model.routes.RoutesSource
import com.euromix.esupervisor.app.model.routes.entities.MapPoint
import com.euromix.esupervisor.app.model.routes.entities.OutletData
import com.euromix.esupervisor.sources.base.BaseRetrofitSource
import com.euromix.esupervisor.sources.base.RetrofitConfig
import com.euromix.esupervisor.sources.routes.entities.MapPointsRequestEntity
import com.euromix.esupervisor.sources.routes.entities.OutletDataRequestEntity
import retrofit2.create
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitRoutesSource @Inject constructor(
    private val config: RetrofitConfig
) : BaseRetrofitSource(config), RoutesSource {

    private val routesApi = retrofit.create<RoutesApi>()
    override suspend fun getRoutes(request: MapPointsRequestEntity): List<MapPoint> {
        return wrapRetrofitException {

            val jsonAdapter = config.moshi.adapter(MapPointsRequestEntity::class.java)
            routesApi.getRoutes(jsonAdapter.toJson(request)).map { it.toMapPoint() }
        }
    }

    override suspend fun getOutletData(request: OutletDataRequestEntity): OutletData {
        return wrapRetrofitException {

            val outletData =
                config.moshi.adapter(OutletDataRequestEntity::class.java).let { jsonAdapter ->
                    routesApi.getOutletData(jsonAdapter.toJson(request)).toOutletData()
                }

            outletData
        }
    }
}