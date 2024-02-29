package com.euromix.esupervisor.app.model.docsEmix.entities

import com.euromix.esupervisor.app.enums.DocEmixOperationType
import com.euromix.esupervisor.app.enums.Status
import java.time.LocalDateTime

data class DocEmix(
    val extId: String,
    val deletionMark: Boolean,
    val posted: Boolean,
    val date: LocalDateTime,
    val number: String,
    val description: String,
    val tradingAgent: String?,
    val operationType: DocEmixOperationType,
    val partner: String,
    val status: Status,
    val sum: Float?,
    val partners: String
)