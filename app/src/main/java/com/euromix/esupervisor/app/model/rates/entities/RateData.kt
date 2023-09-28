package com.euromix.esupervisor.app.model.rates.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
data class RateData(
    val totalFact: Float,
    val totalPlan: Float,
    val rows: List<RateDataRow>
)
@Parcelize
data class RateDataRow(
    val rateObject: String,
    val rateObjectId: String,
    val plan: Float,
    val fact: Float
):Parcelable
