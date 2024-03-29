package com.euromix.esupervisor.sources.salesRate.entities

import com.euromix.esupervisor.app.model.rates.entities.RateData
import com.euromix.esupervisor.app.model.rates.entities.RateDataRow
import com.squareup.moshi.Json
data class RateResponseEntity(
    @field:Json(name = "total_plan") val totalPlan: Float,
    @field:Json(name = "total_fact") val totalFact: Float,
    val rows: List<RateResponseRow>
) {
    fun toRateData(): RateData = RateData(
        totalPlan = totalPlan,
        totalFact = totalFact,
        rows = rows.map { it.toRateDataRow() }
    )
}
data class RateResponseRow(
    @field:Json(name = "object_name") val objectName: String,
    @field:Json(name = "object_id") val objectId: String,
    val plan: Float,
    val fact: Float,
) {
    fun toRateDataRow() = RateDataRow(
        rateObject = objectName,
        rateObjectId = objectId,
        plan = plan,
        fact = fact
    )
}