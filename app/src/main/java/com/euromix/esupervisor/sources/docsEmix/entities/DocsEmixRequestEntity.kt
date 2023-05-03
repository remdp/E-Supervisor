package com.euromix.esupervisor.sources.docsEmix.entities

import com.squareup.moshi.Json

data class DocsEmixRequestEntity(
    @field:Json(name = "start_date") val startDate: String? = null,
    @field:Json(name = "end_date") val endDate: String? = null,
    @field:Json(name = "trading_agent_id") val tradingAgentId: String? = null,
    @field:Json(name = "partner_id") val partnerId: String? = null,
    @field:Json(name = "operation_type") val operationType: String? = null,
    @field:Json(name = "status") val status: String? = null
)
