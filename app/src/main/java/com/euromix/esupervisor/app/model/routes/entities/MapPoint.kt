package com.euromix.esupervisor.app.model.routes.entities

import android.content.Context
import android.graphics.Bitmap
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.utils.bitmapFromDrawableRes
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions

data class MapPoint(
    val outletId: String,
    val latitude: Double,
    val longitude: Double,
    val signs: MapPointSigns
) {
    fun toPointsAnnotationOptions() = PointAnnotationOptions().withPoint(
        Point.fromLngLat(longitude, latitude)
    )
}

data class MapPointSigns(
    val outletTA: Boolean = false,
    val isVisit: Boolean = false,
    val isDistanceVisitOutlet: Boolean = false,
    val isVisitDone: Boolean = false,
    val checkInBounds: Boolean = false,
    val isOrders: Boolean = false,
    val isOutletTT: Boolean = false,
    val isOutletAllTT: Boolean = false,
    val isPromisingOutlet: Boolean = false
) {

    companion object {
        fun bitmapCache(context: Context) = mutableMapOf<MapPointSigns?, Bitmap?>().apply {
            put(
                MapPointSigns(outletTA = true),
                context.bitmapFromDrawableRes(R.drawable.ic_person_grey)
            )
            put(
                MapPointSigns(outletTA = true, isVisit = true, checkInBounds = true),
                context.bitmapFromDrawableRes(R.drawable.ic_person_blue)
            )
            put(
                MapPointSigns(
                    outletTA = true,
                    isVisit = true,
                    checkInBounds = true,
                    isOrders = true
                ),
                context.bitmapFromDrawableRes(R.drawable.ic_person_flag_blue)
            )
            put(
                MapPointSigns(
                    outletTA = true,
                    isVisit = true,
                    isVisitDone = true,
                    checkInBounds = true
                ),
                context.bitmapFromDrawableRes(R.drawable.ic_person_green)
            )
            put(
                MapPointSigns(
                    outletTA = true,
                    isVisit = true,
                    isVisitDone = true,
                    checkInBounds = true,
                    isOrders = true
                ),
                context.bitmapFromDrawableRes(R.drawable.ic_person_flag_green)
            )
            put(
                MapPointSigns(outletTA = true, isVisit = true, isDistanceVisitOutlet = true),
                context.bitmapFromDrawableRes(R.drawable.ic_phone_blue)
            )
            put(
                MapPointSigns(outletTA = true, isVisit = true),
                context.bitmapFromDrawableRes(R.drawable.ic_phone_orange)
            )
            put(
                MapPointSigns(outletTA = true, isVisit = true, isVisitDone = true),
                context.bitmapFromDrawableRes(R.drawable.ic_phone_orange)
            )
            put(
                MapPointSigns(
                    outletTA = true,
                    isVisit = true,
                    isDistanceVisitOutlet = true,
                    isVisitDone = true
                ),
                context.bitmapFromDrawableRes(R.drawable.ic_phone_green)
            )
            put(
                MapPointSigns(outletTA = true, isVisit = true, isOrders = true),
                context.bitmapFromDrawableRes(R.drawable.ic_phone_flag_orange)
            )
            put(
                MapPointSigns(outletTA = true, isVisit = true, isVisitDone = true, isOrders = true),
                context.bitmapFromDrawableRes(R.drawable.ic_phone_flag_orange)
            )
            put(
                MapPointSigns(
                    outletTA = true,
                    isVisit = true,
                    isDistanceVisitOutlet = true,
                    isVisitDone = true,
                    isOrders = true
                ),
                context.bitmapFromDrawableRes(R.drawable.ic_phone_flag_green)
            )
            put(
                MapPointSigns(isOutletTT = true),
                context.bitmapFromDrawableRes(R.drawable.ic_persons_grey)
            )
            put(
                MapPointSigns(isOutletAllTT = true),
                context.bitmapFromDrawableRes(R.drawable.ic_persons_cross_grey)
            )
            put(
                MapPointSigns(isPromisingOutlet = true),
                context.bitmapFromDrawableRes(R.drawable.ic_location_plus)
            )
            put(
                null,
                context.bitmapFromDrawableRes(R.drawable.ic_question_mark_grey)
            )
        }
    }
}