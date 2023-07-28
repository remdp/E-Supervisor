package com.euromix.esupervisor.sources.salesRate.entities

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class RateRequestEntity(
    val rate: Int,
    @field:Json(name = "balance_unit_id") val buId: String?,
    @field:Json(name = "trading_team_id") val ttId: String?,
    @field:Json(name = "trading_agent_id") val taId: String?,
    @field:Json(name = "manufacturer_id") val manufacturerId: String?,
    @field:Json(name = "start_date") val startDate: String,
    @field:Json(name = "end_date") val endDate: String,
    @field:Json(name = "detail_level") val detailLevel: Int,
    val completed: Boolean = false
) : Parcelable

