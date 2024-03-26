package com.euromix.esupervisor.sources.visits.entities

import com.euromix.esupervisor.app.enums.VisitType
import com.euromix.esupervisor.app.model.visits.entities.Visit
import com.squareup.moshi.Json
import java.time.LocalDateTime

data class VisitsResponseEntity(
    @field:Json(name = "ext_id") val extId: String,
    val date: String,
    val number: String,
    val partner: String,
    val address: String,
    @field:Json(name = "check_in") val checkIn: String,
    @field:Json(name = "outlet_time") val outletTime: String,
    @field:Json(name = "order_sum") val orderSum: Float,
    @field:Json(name = "cash_receipt_order_sum") val cashReceiptOrderSum: Float,
    val done: Boolean,
    @field:Json(name = "visit_type") val visitType: Int,
    val unscheduled: Boolean,
    @field:Json(name = "check_in_bounds") val checkInBounds: Boolean,
    @field:Json(name = "can_be_changed") val canBeChanged: Boolean
) {
    fun toVisit() = Visit(
        extId = extId,
        date = LocalDateTime.parse(date),
        number = number,
        partner = partner,
        address = address,
        checkIn = checkIn,
        outletTime = outletTime,
        orderSum = orderSum,
        cashReceiptOrderSum = cashReceiptOrderSum,
        done = done,
        type = VisitType.getByIndex(visitType),
        unscheduled = unscheduled,
        checkInBounds = checkInBounds,
        canBeChanged = canBeChanged
    )
}
