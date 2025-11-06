package com.euromix.esupervisor.app.model.odometers.entities

import java.time.LocalDateTime

data class OdometersReading(
    val todayReadings: Int,
    val readings: List<OdometersReadingItem>
)

data class OdometersReadingItem(
    val id: Int,
    val date: LocalDateTime,
    val startKm: String = "",
    val stopKm: String = "",
    val startTime: LocalDateTime,
    val stopTime: LocalDateTime? = null,
    val driver: String,
    val mileage: Int,
    val startPhoto: String? = null,
    val stopPhoto: String? = null,
    val carNumber: String
)