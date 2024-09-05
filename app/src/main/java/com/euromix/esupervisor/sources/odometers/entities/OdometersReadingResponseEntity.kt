package com.euromix.esupervisor.sources.odometers.entities

import com.euromix.esupervisor.app.model.odometers.entities.OdometersReading
import com.squareup.moshi.Json
import java.time.LocalDateTime

data class OdometersReadingResponseEntity(
    val date: String,
    @field:Json(name = "start_time") val startTime: String,
    @field:Json(name = "stop_time") val stopTime: String? = null,
    val driver: String,
    val mileage: Int,
    @field:Json(name = "start_photo") val startPhoto: String? = null,
    @field:Json(name = "stop_photo") val stopPhoto: String? = null
) {

    fun toOdometersReading() = OdometersReading(
        date = LocalDateTime.parse(date),
        startTime = LocalDateTime.parse(startTime),
        stopTime = LocalDateTime.parse(stopTime),
        driver = driver,
        mileage = mileage,
        startPhoto = startPhoto,
        stopPhoto = stopPhoto
    )
}