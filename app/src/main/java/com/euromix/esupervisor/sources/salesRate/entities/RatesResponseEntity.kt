package com.euromix.esupervisor.sources.salesRate.entities

import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.rates.entities.RateStructure
import com.squareup.moshi.Json

data class RatesResponseEntity(
    val rate: ServerPair,
    val dimensions: List<String>,
    @field:Json(name = "day_dimensions") val dayDimensions: List<String>,
) {
    fun toRateStructure(): RateStructure =
        RateStructure(
            rate = rate,
            dimensions = dimensions,
            dayDimensions = dayDimensions
        )
}