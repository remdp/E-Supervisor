package com.euromix.esupervisor.app.model.odometers.entities

import java.time.LocalDateTime

data class TodayOdometersReading(
    val startKm: Int?,
    val startUri: String?,
    val startTime: LocalDateTime?,
    val startPhoto: String? = null,
    val stopKm: Int?,
    val stopUri: String?,
    val stopTime: LocalDateTime?,
    val stopPhoto: String? = null
)
