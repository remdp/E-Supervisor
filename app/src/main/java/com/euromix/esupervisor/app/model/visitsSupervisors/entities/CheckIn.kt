package com.euromix.esupervisor.app.model.visitsSupervisors.entities

data class CheckIn(
    val isCheckIn: Boolean,
    val outlet: String,
    val longitude: Double,
    val latitude: Double,
    val checkInPhoto: String,
    val checkInDeviation: Int,
    val checkInByPhoto: Boolean
)

