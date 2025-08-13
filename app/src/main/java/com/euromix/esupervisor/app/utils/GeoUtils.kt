package com.euromix.esupervisor.app.utils

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun distanceBetweenPoints(
    longitude1: Double?, latitude1: Double?, longitude2: Double?, latitude2: Double?
): Int {
    val pi = Math.PI
    val earthRadius = 6372795

    if (longitude1 == null || latitude1 == null || longitude2 == null || latitude2 == null) {
        return 0
    }

    val longitude1Rad = pi * longitude1 / 180
    val longitude2Rad = pi * longitude2 / 180
    val latitude1Rad = pi * latitude1 / 180
    val latitude2Rad = pi * latitude2 / 180

    val deltaLongitude = abs(longitude2Rad - longitude1Rad)

    val distance = earthRadius * atan(
        sqrt(
            cos(latitude2Rad) * sin(deltaLongitude).pow(2) +
                    (cos(latitude1Rad) * sin(latitude2Rad) - sin(latitude1Rad) * cos(latitude2Rad) * cos(
                        deltaLongitude
                    )).pow(2)
        ) /
                (sin(latitude1Rad) * sin(latitude2Rad) + cos(latitude1Rad) * cos(latitude2Rad) * cos(
                    deltaLongitude
                ))
    )

    return abs(distance.roundToInt())
}