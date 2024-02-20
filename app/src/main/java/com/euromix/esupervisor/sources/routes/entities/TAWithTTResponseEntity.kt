package com.euromix.esupervisor.sources.routes.entities

import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.squareup.moshi.Json

data class TAWithTTResponseEntity(
    @field:Json(name = "trading_agent") val tradingAgent: ServerPair,
    @field:Json(name = "trading_team") val tradingTeam: ServerPair
) {

    fun toTradingAgentsAndTeams() =
        TradingAgentsAndTeams(tradingAgent = tradingAgent, tradingTeam = tradingTeam)

}

