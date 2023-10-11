package com.euromix.esupervisor.sources.salesRate.entities

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize
@Parcelize
data class RateRequestEntity(
    @field:Json(name = "rate_id") val rateId: String,
    @field:Json(name = "start_date") val startDate: String?,
    @field:Json(name = "end_date") val endDate: String?,
    @field:Json(name = "detail_level") val detailLevel: Int,
    val selection: List<RateSelectionItem>,
) : Parcelable

@Parcelize
data class RateSelectionItem(
    val id: String, val serverType: String
) : Parcelable

