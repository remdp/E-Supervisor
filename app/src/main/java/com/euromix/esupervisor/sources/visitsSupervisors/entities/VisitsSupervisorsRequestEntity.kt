package com.euromix.esupervisor.sources.visitsSupervisors.entities

import com.squareup.moshi.Json
data class VisitsSupervisorsRequestEntity(
    @field:Json(name = "start_date") val startDate: String? = null,
    @field:Json(name = "end_date") val endDate: String? = null,
    @field:Json(name = "only_my_visits") val onlyMyVisits: Boolean = false,
    @field:Json(name = "supervisors") val supervisors: List<String> = listOf()
)
