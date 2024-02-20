package com.euromix.esupervisor.sources.routes.entities

import com.euromix.esupervisor.app.model.routes.entities.MapPoint
import com.euromix.esupervisor.app.model.routes.entities.MapPointSigns
import com.squareup.moshi.Json

data class MapPointsResponseEntity(

    @field:Json(name = "outlet_id") val outletId: String,
    val latitude: Double,
    val longitude: Double,

    @field:Json(name = "outlet_ta") val outletTA: Boolean = false,
    @field:Json(name = "is_visit") val isVisit: Boolean = false,
    @field:Json(name = "is_distance_visit_outlet") val isDistanceVisitOutlet: Boolean = false,
    @field:Json(name = "is_visit_done") val isVisitDone: Boolean = false,
    @field:Json(name = "check_in_bounds") val checkInBounds: Boolean = false,
    @field:Json(name = "is_orders") val isOrders: Boolean = false,
    @field:Json(name = "is_outlet_tt") val isOutletTT: Boolean = false,
    @field:Json(name = "is_outlet_all_tt") val isOutletAllTT: Boolean = false,
    @field:Json(name = "is_promising_outlet") val isPromisingOutlet: Boolean = false
) {

    fun toMapPoint() = MapPoint(
        outletId = outletId,
        latitude = latitude,
        longitude = longitude,
        signs = MapPointSigns(
            outletTA = outletTA,
            isVisit = isVisit,
            isDistanceVisitOutlet = isDistanceVisitOutlet,
            isVisitDone = isVisitDone,
            checkInBounds = checkInBounds,
            isOrders = isOrders,
            isOutletTT = isOutletTT,
            isOutletAllTT = isOutletAllTT,
            isPromisingOutlet = isPromisingOutlet

        )
    )
}