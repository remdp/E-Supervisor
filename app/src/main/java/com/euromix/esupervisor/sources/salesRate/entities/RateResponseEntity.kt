package com.euromix.esupervisor.sources.salesRate.entities

import com.euromix.esupervisor.app.model.rates.entities.RateData
import com.squareup.moshi.Json

data class RateResponseEntity(
    @field:Json(name = "object_name") val objectName: String,
    @field:Json(name = "trading_agent_id") val tradingAgentId: String,
    val plan: Float,
    val fact: Float,
) {
    fun toRateData(): RateData = RateData(
        rateObject = objectName,
        tradingAgentId = tradingAgentId,
        plan = plan,
        fact = fact
    )
}