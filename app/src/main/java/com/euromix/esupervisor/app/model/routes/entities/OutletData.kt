package com.euromix.esupervisor.app.model.routes.entities

data class OutletData (
    val partners: List<String>,
    val name: String,
    val address: String,
    val checkIn: String,
    val outletTime: String,
    val orderSum: String,
    val cashReceiptOrderSum: String
)
