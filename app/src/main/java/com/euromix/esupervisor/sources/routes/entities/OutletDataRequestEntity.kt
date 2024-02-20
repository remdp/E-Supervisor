package com.euromix.esupervisor.sources.routes.entities

import com.squareup.moshi.Json
data class OutletDataRequestEntity(
    @field:Json(name = "start_date") val startDate: String,
    @field:Json(name = "end_date") val endDate: String,
    @field:Json(name = "outlet_id") val outletId: String
)
