package com.euromix.esupervisor.app.model.routes.entities

import android.content.Context
import android.graphics.Bitmap
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.utils.bitmapFromDrawableRes
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
        fun bitmapCache(context: Context) = mutableMapOf<MapPointSigns?, Bitmap?>().apply {
            put(
                MapPointSigns(outletTA = true),
                context.bitmapFromDrawableRes(drawableResForSigns(outletTA = true))
            )
            put(
                MapPointSigns(outletTA = true, isVisit = true, checkInBounds = true),
                context.bitmapFromDrawableRes(
                    drawableResForSigns(
                        outletTA = true, isVisit = true, checkInBounds = true
                    )
                )
            )
            put(
                MapPointSigns(
                    outletTA = true, isVisit = true, checkInBounds = true, isOrders = true
                ), context.bitmapFromDrawableRes(
                    drawableResForSigns(
                        outletTA = true, isVisit = true, checkInBounds = true, isOrders = true
                    )
                )
            )
            put(
                MapPointSigns(
                    outletTA = true, isVisit = true, isVisitDone = true, checkInBounds = true
                ), context.bitmapFromDrawableRes(
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
                ), context.bitmapFromDrawableRes(
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
                context.bitmapFromDrawableRes(
                    drawableResForSigns(
                        outletTA = true, isVisit = true, isDistanceVisitOutlet = true
                    )
                )
            )
            put(
                MapPointSigns(outletTA = true, isVisit = true), context.bitmapFromDrawableRes(
                    drawableResForSigns(
                        outletTA = true, isVisit = true
                    )
                )
            )
            put(
                MapPointSigns(outletTA = true, isVisit = true, isVisitDone = true),
                context.bitmapFromDrawableRes(
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
                ), context.bitmapFromDrawableRes(
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
                context.bitmapFromDrawableRes(
                    drawableResForSigns(
                        outletTA = true, isVisit = true, isOrders = true
                    )
                )
            )
            put(
                MapPointSigns(outletTA = true, isVisit = true, isVisitDone = true, isOrders = true),
                context.bitmapFromDrawableRes(
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
                ), context.bitmapFromDrawableRes(
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
                MapPointSigns(isOutletTT = true), context.bitmapFromDrawableRes(
                    drawableResForSigns(
                        isOutletTT = true
                    )
                )
            )
            put(
                MapPointSigns(isOutletAllTT = true), context.bitmapFromDrawableRes(
                    drawableResForSigns(
                        isOutletAllTT = true
                    )
                )
            )
            put(
                MapPointSigns(isPromisingOutlet = true), context.bitmapFromDrawableRes(
                    drawableResForSigns(
                        isPromisingOutlet = true
                    )
                )
            )
            put(
                null, context.bitmapFromDrawableRes(drawableResForSigns())
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

                outletTA && isVisit && !checkInBounds && !isVisitDone && !isOrders && !isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_phone_orange

                outletTA && isVisit && !checkInBounds && isVisitDone && !isOrders && !isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_phone_orange

                outletTA && isVisit && !checkInBounds && isVisitDone && !isOrders && isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_phone_green

                outletTA && isVisit && !checkInBounds && !isVisitDone && isOrders && !isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_phone_flag_orange

                outletTA && isVisit && !checkInBounds && isVisitDone && isOrders && !isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_phone_flag_orange

                outletTA && isVisit && !checkInBounds && isVisitDone && isOrders && isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_phone_flag_green

                !outletTA && !isVisit && !checkInBounds && !isVisitDone && !isOrders && !isDistanceVisitOutlet && isOutletTT && !isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_persons_grey

                !outletTA && !isVisit && !checkInBounds && !isVisitDone && !isOrders && !isDistanceVisitOutlet && !isOutletTT && isOutletAllTT && !isPromisingOutlet -> R.drawable.ic_persons_cross_grey

                !outletTA && !isVisit && !checkInBounds && !isVisitDone && !isOrders && !isDistanceVisitOutlet && !isOutletTT && !isOutletAllTT && isPromisingOutlet -> R.drawable.ic_location_plus

                else -> R.drawable.ic_question_mark_grey
            }
        }
    }
}