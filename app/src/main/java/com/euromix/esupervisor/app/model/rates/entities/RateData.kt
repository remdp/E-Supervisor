package com.euromix.esupervisor.app.model.rates.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RateData(
    val rateObject: String,
    val rateObjectId: String,
    val plan: Double,
    val fact: Double
):Parcelable
