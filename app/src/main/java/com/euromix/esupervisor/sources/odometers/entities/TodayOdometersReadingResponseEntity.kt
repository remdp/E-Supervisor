package com.euromix.esupervisor.sources.odometers.entities

import com.euromix.esupervisor.app.model.odometers.entities.TodayOdometersReading
import com.squareup.moshi.Json
import java.time.LocalDateTime

data class TodayOdometersReadingResponseEntity(
    @field:Json(name = "start_km") val startKm: Int? = null,
    @field:Json(name = "start_uri") val startUri: String? = null,
    @field:Json(name = "start_time") val startTime: String? = null,
    @field:Json(name = "start_photo") val startPhoto: String? = null,
    @field:Json(name = "stop_km") val stopKm: Int? = null,
    @field:Json(name = "stop_uri") val stopUri: String? = null,
    @field:Json(name = "stop_time") val stopTime: String? = null,
    @field:Json(name = "stop_photo") val stopPhoto: String? = null
) {

    fun toOdometersReading() = TodayOdometersReading(
        startKm = startKm,
        startUri = startUri,
        startTime = startTime?.let { LocalDateTime.parse(it) },
        startPhoto = startPhoto,
        stopKm = stopKm,
        stopUri = stopUri,
        stopTime = stopTime?.let { LocalDateTime.parse(it) },
        stopPhoto = stopPhoto
    )

}