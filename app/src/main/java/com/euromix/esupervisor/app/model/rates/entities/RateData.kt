package com.euromix.esupervisor.app.model.rates.entities

import android.os.Parcelable
import com.euromix.esupervisor.app.model.common.entities.ServerObject
import kotlinx.parcelize.Parcelize
data class RateData(
    val totalFact: Float,
    val totalPlan: Float,
    val rows: List<RateDataRow>
)
@Parcelize
data class RateDataRow(
    val serverObject: ServerObject,
    val plan: Float,
    val fact: Float
):Parcelable
