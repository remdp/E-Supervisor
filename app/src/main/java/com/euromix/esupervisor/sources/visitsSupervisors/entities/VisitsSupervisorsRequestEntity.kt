package com.euromix.esupervisor.sources.visitsSupervisors.entities

import com.squareup.moshi.Json
data class VisitsSupervisorsRequestEntity(
    @field:Json(name = "start_date") val startDate: String? = null,
    @field:Json(name = "end_date") val endDate: String? = null
)
