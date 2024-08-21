package com.euromix.esupervisor.sources.routes.entities

import com.squareup.moshi.Json

data class RoutesStatisticRequestEntity(
    @field:Json(name = "start_date") val startDate: String,
    @field:Json(name = "end_date") val endDate: String,
    @field:Json(name = "balance_unit_id") val balanceUnitId: String?,
    @field:Json(name = "trading_agent_id") val tradingAgentId: String?,
    @field:Json(name = "trading_team_id") val tradingTeamId: String?,
    @field:Json(name = "detail_level") val detailLevel: Int
)
