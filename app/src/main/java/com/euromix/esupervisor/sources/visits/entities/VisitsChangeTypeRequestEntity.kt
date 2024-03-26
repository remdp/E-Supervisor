package com.euromix.esupervisor.sources.visits.entities

import com.squareup.moshi.Json

data class VisitsChangeTypeRequestEntity(
    @field:Json(name = "visit_type") val visitType: Int,
    @field:Json(name = "visits_ids") val visitsIds: List<String>,
    @field:Json(name = "reason_id") val reasonId: String,
    val comment: String = ""
)
