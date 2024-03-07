package com.euromix.esupervisor.sources.visits.entities

import com.euromix.esupervisor.app.model.visits.entities.Visit
import com.squareup.moshi.Json

data class VisitsResponseEntity(
    @field:Json(name = "ext_id") val extId: String,
    val partner: String,
    val address: String,
    @field:Json(name = "check_in") val checkIn: String,
    @field:Json(name = "outlet_time") val outletTime: String,
    @field:Json(name = "order_sum") val orderSum: Float,
    @field:Json(name = "cash_receipt_order_sum") val cashReceiptOrderSum: Float
){
    fun toVisit() = Visit(
        extId = extId,
        partner = partner,
        address = address,
        checkIn = checkIn,
        outletTime = outletTime,
        orderSum = orderSum,
        cashReceiptOrderSum = cashReceiptOrderSum
    )
}
