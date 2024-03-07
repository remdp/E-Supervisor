package com.euromix.esupervisor.app.model.visits.entities

data class Visit(
val extId:  String,
val partner: String,
val address: String,
val checkIn: String,
val outletTime: String,
val orderSum: Float,
val cashReceiptOrderSum: Float
)
