package com.euromix.esupervisor.sources.salesRate.entities

import com.euromix.esupervisor.app.model.rates.entities.RateData
import com.squareup.moshi.Json

data class RateResponseEntity(
    @field:Json(name = "object_name") val objectName: String,
    @field:Json(name = "object_id") val objectId: String,
    val plan: Double,
    val fact: Double,
) {
    fun toRateData(): RateData = RateData(
        rateObject = objectName,
        rateObjectId = objectId,
        plan = plan,
        fact = fact
    )
}