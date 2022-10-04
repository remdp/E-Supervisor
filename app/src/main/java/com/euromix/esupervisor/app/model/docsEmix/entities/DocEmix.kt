package com.euromix.esupervisor.app.model.docsEmix.entities

import java.time.LocalDateTime
import java.util.*

data class DocEmix(
    val extId: String,
    val deletionMark: Boolean,
    val posted: Boolean,
    val date: LocalDateTime,
    val number: String,
    val description: String,
    val tradingAgent: String?,
    val operationType: String?,
    val partner: String,
    val status: String
)