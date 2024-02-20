package com.euromix.esupervisor.sources.routes.entities

import com.euromix.esupervisor.app.model.routes.entities.OutletData
import com.squareup.moshi.Json

data class OutletDataResponseEntity(
    val partners: List<String>,
    val name: String,
    val address: String,
    @field:Json(name = "check_in") val checkIn: String,
    @field:Json(name = "outlet_time") val outletTime: String,
    @field:Json(name = "order_sum") val orderSum: String,
    @field:Json(name = "cash_receipt_order_sum") val cashReceiptOrderSum: String
) {

    fun toOutletData() = OutletData(
        partners = partners,
        name = name,
        address = address,
        checkIn = checkIn,
        outletTime = outletTime,
        orderSum = orderSum,
        cashReceiptOrderSum = cashReceiptOrderSum
    )
}
