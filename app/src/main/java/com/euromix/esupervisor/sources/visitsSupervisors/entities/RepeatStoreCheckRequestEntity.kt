package com.euromix.esupervisor.sources.visitsSupervisors.entities

import com.squareup.moshi.Json

data class RepeatStoreCheckRequestEntity(
    val date:String,
    @field:Json(name = "visits_supervisor_ids") val visitsSupervisorIds: List<String>
)
