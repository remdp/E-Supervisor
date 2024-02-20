package com.euromix.esupervisor.sources.routes.entities

import com.euromix.esupervisor.app.model.common.entities.ServerPair

data class TradingAgentsAndTeams(
    val tradingAgent: ServerPair,
    val tradingTeam: ServerPair
)
