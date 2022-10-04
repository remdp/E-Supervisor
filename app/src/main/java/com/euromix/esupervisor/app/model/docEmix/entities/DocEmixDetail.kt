package com.euromix.esupervisor.app.model.docEmix.entities

import java.time.LocalDateTime

data class DocEmixDetail(
    val extId: String,
    val date: LocalDateTime,
    val number: String,
    val description: String,
    val tradingAgent: String,
    val operationType: String,
    val partner: String,
    val status: String,
    val canBeAgreed : Boolean,
    val rowTradeConditions: List<RowTradeCondition>?
)

data class RowTradeCondition(
    val row: Int,
    val manufacturer: String,
    val priceIndex: Int,
    val paymentDeferment: Int,
    val distributionChannel: String
)