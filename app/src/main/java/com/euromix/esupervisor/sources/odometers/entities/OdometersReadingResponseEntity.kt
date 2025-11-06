package com.euromix.esupervisor.sources.odometers.entities

import com.euromix.esupervisor.app.model.odometers.entities.OdometersReading
import com.euromix.esupervisor.app.model.odometers.entities.OdometersReadingItem
import com.squareup.moshi.Json
import java.time.LocalDateTime


data class OdometersReadingResponseEntity(
    val todayReadings: Int,
    val readings: List<OdometersReadingResponseEntityItem>
) {
    fun toOdometersReading() = OdometersReading(
        todayReadings = todayReadings,
        readings = readings.mapIndexed { index, it ->
            OdometersReadingItem(
                id = index,
                date = LocalDateTime.parse(it.date),
                startKm = it.startKm,
                stopKm = it.stopKm,
                startTime = LocalDateTime.parse(it.startTime),
                stopTime = LocalDateTime.parse(it.stopTime),
                driver = it.driver,
                mileage = it.mileage,
                startPhoto = it.startPhoto,
                stopPhoto = it.stopPhoto,
                carNumber = it.carNumber
            )
        }
    )
}

data class OdometersReadingResponseEntityItem(
    val date: String,
    @field:Json(name = "start_km") val startKm: String = "",
    @field:Json(name = "stop_km") val stopKm: String = "",
    @field:Json(name = "start_time") val startTime: String,
    @field:Json(name = "stop_time") val stopTime: String? = null,
    val driver: String,
    val mileage: Int,
    @field:Json(name = "start_photo") val startPhoto: String? = null,
    @field:Json(name = "stop_photo") val stopPhoto: String? = null,
    @field:Json(name = "car_number") val carNumber: String = ""
)