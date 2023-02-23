package com.euromix.esupervisor.sources.salesRate.entities

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class RateRequestEntity(
    val rate: String,
    val tradingAgentId: String? = null,
    @field:Json(name = "start_date") val startDate: String,
    @field:Json(name = "end_date") val endDate: String,
    val completed: Boolean = false
):Parcelable

