package com.euromix.esupervisor.sources.docsEmix.entities

import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import com.squareup.moshi.Json
import java.time.LocalDateTime

data class DocsEmixResponseEntity(
    val extId: String,
    val deletionMark: Boolean,
    val posted: Boolean,
    val date: String,
    val number: String,
    val description: String,
    @field:Json(name = "trading_agent") val tradingAgent: String?,
    @field:Json(name = "operation_type") val operationType: String?,
    val partner: String,
    val status: String
) {

    fun toDocEmix(): DocEmix = DocEmix(
        extId = extId,
        deletionMark = deletionMark,
        posted = posted,
        date = LocalDateTime.parse(date),
        number = number,
        description = description,
        tradingAgent = tradingAgent,
        operationType = operationType,
        partner = partner,
        status = status
    )

}