package com.euromix.esupervisor.sources.routes.entities

import com.euromix.esupervisor.app.model.common.entities.ServerObject
import com.euromix.esupervisor.app.model.routes.entities.VisitsStatisticData
import com.squareup.moshi.Json

data class VisitsStatisticsResponseEntity(

    @field:Json(name = "server_object") val serverObject: ServerObject,
    @field:Json(name = "children_count") val childrenCount: Int = 0,
    @field:Json(name = "plan_visits") val planVisits: PlanFact,
    @field:Json(name = "unscheduled_visits") val unscheduledVisits: PlanFact,
    @field:Json(name = "regular_visits") val regularVisits: PlanFact,
    @field:Json(name = "remote_constant_visits") val remoteConstantVisits: PlanFact,
    @field:Json(name = "remote_situational_visits") val remoteSituationalVisits: PlanFact,
    @field:Json(name = "effective_visits") val effectiveVisits: PlanFact,
    @field:Json(name = "outlets_time") val outletsTime: String,
    @field:Json(name = "travel_time") val travelTime: String
) {
    fun toVisitsStatisticsData() = VisitsStatisticData(
        serverObject = serverObject,
        childrenCount = childrenCount,
        planVisits = planVisits,
        unscheduledVisits = unscheduledVisits,
        regularVisits = regularVisits,
        remoteConstantVisits = remoteConstantVisits,
        remoteSituationalVisits = remoteSituationalVisits,
        effectiveVisits = effectiveVisits,
        outletsTime = outletsTime,
        travelTime = travelTime
    )
}

data class PlanFact(
    val plan: Int = 0,
    val fact: Int = 0
)
