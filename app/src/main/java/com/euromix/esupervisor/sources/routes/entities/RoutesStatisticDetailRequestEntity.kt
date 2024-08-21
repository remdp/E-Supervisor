package com.euromix.esupervisor.sources.routes.entities

import com.euromix.esupervisor.app.model.common.entities.ServerSelectionItem
import com.squareup.moshi.Json

data class RoutesStatisticDetailRequestEntity(
    @field:Json(name = "start_date") val startDate: String,
    @field:Json(name = "end_date") val endDate: String,
    val selection: ServerSelectionItem
)
