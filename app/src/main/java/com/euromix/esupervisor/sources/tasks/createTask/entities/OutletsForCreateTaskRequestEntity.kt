package com.euromix.esupervisor.sources.tasks.createTask.entities

import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.squareup.moshi.Json

data class OutletsForCreateTaskRequestEntity(
    @field:Json(name = "trading_agents") val tradingAgents: List<String>? = null,
    @field:Json(name = "outlets_inner_types") val outletsInnerTypes: List<String>? = null
    //@field:Json(name = "partners_ids") val partnersIds: List<String>? = null
) {
    //fun isEmpty() = outletsInnerTypes?.isEmpty() ?: true && partnersIds?.isEmpty() ?: true
    fun isEmpty() = tradingAgents?.isEmpty() ?: true && outletsInnerTypes?.isEmpty() ?: true
}
