package com.euromix.esupervisor.app.model.routes.entities

import android.graphics.Bitmap
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.utils.ResourceManager
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions

data class MapPoint(
    val outletId: String, val latitude: Double, val longitude: Double, val signs: MapPointSigns
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
        fun bitmapCache(resManager: ResourceManager) = mutableMapOf<MapPointSigns?, Bitmap?>().apply {
            put(
                MapPointSigns(outletTA = true),
                resManager.getBitmapFromDrawableRes(drawableResForSigns(outletTA = true))
            )
            put(
                MapPointSigns(outletTA = true, isVisit = true, checkInBounds = true),
                resManager.getBitmapFromDrawableRes(
                    drawableResForSigns(
                        outletTA = true, isVisit = true, checkInBounds = true
                    )
                )
            )
            put(
                MapPointSigns(
                    outletTA = true, isVisit = true, checkInBounds = true, isOrders = true
                ), resManager.getBitmapFromDrawableRes(
                    drawableResForSigns(
                        outletTA = true, isVisit = true, checkInBounds = true, isOrders = true
                    )
                )
            )
            put(
                MapPointSigns(
                    outletTA = true, isVisit = true, isVisitDone = true, checkInBounds = true
                ), resManager.getBitmapFromDrawableRes(
                    drawableResForSigns(
                        outletTA = true, isVisit = true, isVisitDone = true, checkInBounds = true
                    )
                )
            )
            put(
                MapPointSigns(
                    outletTA = true,
                    isVisit = true,
                    isVisitDone = true,
                    checkInBounds = true,
                    isOrders = true
                ), resManager.getBitmapFromDrawableRes(
                    drawableResForSigns(
                        outletTA = true,
                        isVisit = true,
                        isVisitDone = true,
                        checkInBounds = true,
                        isOrders = true
                    )
                )
            )
            put(
                MapPointSigns(outletTA = true, isVisit = true, isDistanceVisitOutlet = true),
                resManager.getBitmapFromDrawableRes(
                    drawableResForSigns(
                        outletTA = true, isVisit = true, isDistanceVisitOutlet = true
                    )
                )
            )
            put(
                MapPointSigns(outletTA = true, isVisit = true), resManager.getBitmapFromDrawableRes(
                    drawableResForSigns(
                        outletTA = true, isVisit = true
                    )
                )
            )
            put(
                MapPointSigns(outletTA = true, isVisit = true, isVisitDone = true),
                resManager.getBitmapFromDrawableRes(
                    drawableResForSigns(
                        outletTA = true, isVisit = true, isVisitDone = true
                    )
                )
            )
            put(
                MapPointSigns(
                    outletTA = true,
                    isVisit = true,
                    isDistanceVisitOutlet = true,
                    isVisitDone = true
                ), resManager.getBitmapFromDrawableRes(
                    drawableResForSigns(
                        outletTA = true,
                        isVisit = true,
                        isDistanceVisitOutlet = true,
                        isVisitDone = true
                    )
                )
            )
            put(
                MapPointSigns(outletTA = true, isVisit = true, isOrders = true),
                resManager.getBitmapFromDrawableRes(
                    drawableResForSigns(
                        outletTA = true, isVisit = true, isOrders = true
                    )
                )
            )
            put(
                MapPointSigns(outletTA = true, isVisit = true, isVisitDone = true, isOrders = true),
                resManager.getBitmapFromDrawableRes(
                    drawableResForSigns(
                        outletTA = true, isVisit = true, isVisitDone = true, isOrders = true
                    )
                )
            )
            put(
                MapPointSigns(
                    outletTA = true,
                    isVisit = true,
                    isDistanceVisitOutlet = true,
                    isVisitDone = true,
                    isOrders = true
                ), resManager.getBitmapFromDrawableRes(
                    drawableResForSigns(
                        outletTA = true,
                        isVisit = true,
                        isDistanceVisitOutlet = true,
                        isVisitDone = true,
                        isOrders = true
                    )
                )
            )
            put(
                MapPointSigns(isOutletTT = true), resManager.getBitmapFromDrawableRes(
                    drawableResForSigns(
                        isOutletTT = true
                    )
                )
            )
            put(
                MapPointSigns(isOutletAllTT = true), resManager.getBitmapFromDrawableRes(
                    drawableResForSigns(
                        isOutletAllTT = true
                    )
                )
            )
            put(
                MapPointSigns(isPromisingOutlet = true), resManager.getBitmapFromDrawableRes(
                    drawableResForSigns(
                        isPromisingOutlet = true
                    )
                )
            )
            put(
                null, resManager.getBitmapFromDrawableRes(drawableResForSigns())
            )
        }

        fun drawableResForSigns(
            outletTA: Boolean = false,
            isVisit: Boolean = false,
            checkInBounds: Boolean = false,
            isVisitDone: Boolean = false,
            isOrders: Boolean = false,
            isDistanceVisitOutlet: Boolean = false,
            isOutletTT: Boolean = false,
            isOutletAllTT: Boolean = false,
            isPromisingOutlet: Boolean = false
        ): Int {
            return when {
                outletTA && !isVisit && !checkInBounds && !isVisitDone && !isOrders && !isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_person_grey

                outletTA && isVisit && checkInBounds && !isVisitDone && !isOrders && !isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_person_blue

                outletTA && isVisit && checkInBounds && !isVisitDone && isOrders && !isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_person_flag_blue

                outletTA && isVisit && checkInBounds && isVisitDone && !isOrders && !isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_person_green

                outletTA && isVisit && checkInBounds && isVisitDone && isOrders && !isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_person_flag_green

                outletTA && isVisit && !checkInBounds && !isVisitDone && !isOrders && isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_phone_blue

                outletTA && isVisit && !checkInBounds && !isVisitDone && !isOrders && !isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_person_orange

                outletTA && isVisit && !checkInBounds && isVisitDone && !isOrders && !isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_person_orange

                outletTA && isVisit && !checkInBounds && isVisitDone && !isOrders && isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_phone_green

                outletTA && isVisit && !checkInBounds && !isVisitDone && isOrders && !isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_person_flag_orange

                outletTA && isVisit && !checkInBounds && isVisitDone && isOrders && !isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_person_flag_orange

                outletTA && isVisit && !checkInBounds && isVisitDone && isOrders && isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_phone_flag_green

                !outletTA && !isVisit && !checkInBounds && !isVisitDone && !isOrders && !isDistanceVisitOutlet && isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_persons_grey

                !outletTA && !isVisit && !checkInBounds && !isVisitDone && !isOrders && !isDistanceVisitOutlet && !isOutletTT && isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_persons_cross_grey

                !outletTA && !isVisit && !checkInBounds && !isVisitDone && !isOrders && !isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && isPromisingOutlet -> R.drawable.ic_location_plus

                else -> R.drawable.ic_question_mark_grey
            }
        }
    }
}