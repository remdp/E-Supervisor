package com.euromix.esupervisor.app.model.rates.entities

import com.euromix.esupervisor.app.model.common.entities.ServerPair

data class RateStructure(
    val rate: ServerPair,
    val dimensions: List<String>
)
