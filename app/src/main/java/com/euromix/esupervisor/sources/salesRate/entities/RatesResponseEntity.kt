package com.euromix.esupervisor.sources.salesRate.entities

import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.rates.entities.RateStructure

data class RatesResponseEntity(
    val rate: ServerPair,
    val dimensions: List<String>
) {
    fun toRateStructure(): RateStructure =
        RateStructure(
            rate = rate,
            dimensions = dimensions
        )
}