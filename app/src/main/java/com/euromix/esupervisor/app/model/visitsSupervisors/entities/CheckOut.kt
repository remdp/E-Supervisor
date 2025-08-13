package com.euromix.esupervisor.app.model.visitsSupervisors.entities

data class CheckOut(
    val isCheckOut: Boolean,
    val outlet: String,
    val longitude: Double,
    val latitude: Double,
    val outletTime: Int
)

