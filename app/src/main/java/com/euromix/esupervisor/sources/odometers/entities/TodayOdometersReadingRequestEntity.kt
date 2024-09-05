package com.euromix.esupervisor.sources.odometers.entities

data class TodayOdometersReadingRequestEntity(
    val start: Boolean,
    val km: Int,
    val photo: String
)