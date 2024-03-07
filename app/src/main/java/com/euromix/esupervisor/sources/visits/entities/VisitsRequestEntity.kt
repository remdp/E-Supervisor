package com.euromix.esupervisor.sources.visits.entities

import com.squareup.moshi.Json
data class VisitsRequestEntity(
    @field:Json(name = "start_date") val startDate: String? = null,
    @field:Json(name = "end_date") val endDate: String? = null,
    @field:Json(name = "trading_agent_id") val tradingAgentId: String? = null
)
