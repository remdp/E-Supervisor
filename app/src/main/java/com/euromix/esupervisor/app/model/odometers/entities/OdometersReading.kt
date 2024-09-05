package com.euromix.esupervisor.app.model.odometers.entities

import java.time.LocalDateTime

data class OdometersReading(
    val date: LocalDateTime,
    val startTime: LocalDateTime,
    val stopTime: LocalDateTime? = null,
    val driver: String,
    val mileage: Int,
    val startPhoto: String? = null,
    val stopPhoto: String? = null
)
