package com.euromix.esupervisor.sources.odometers.entities

import com.squareup.moshi.Json

data class OdometersReadingRequestEntity(
    @field:Json(name = "start_date") val startDate: String? = null,
    @field:Json(name = "end_date") val endDate: String? = null
)
