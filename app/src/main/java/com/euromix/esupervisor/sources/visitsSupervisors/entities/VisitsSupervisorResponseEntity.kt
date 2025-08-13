package com.euromix.esupervisor.sources.visitsSupervisors.entities

import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitSupervisor
import com.squareup.moshi.Json
import java.time.LocalDateTime

data class VisitsSupervisorResponseEntity(
    val id: String,
    val date: String,
    val number: String,
    val outlet: String,
    val partner: String,
    @field:Json(name = "outlet_verified") val outletVerified: Boolean,
    @field:Json(name = "field_visit_type") val fieldVisitType: Int, //0 - store check, 1 - field training
    @field:Json(name = "is_checkin") val isCheckIn: Boolean,
    @field:Json(name = "is_checkout") val isCheckOut: Boolean,
    @field:Json(name = "is_done") val isDone: Boolean
) {
    fun toVisitSupervisor() = VisitSupervisor(
        id = id,
        date = LocalDateTime.parse(date),
        number = number,
        outlet = outlet,
        partner = partner,
        outletVerified = outletVerified,
        fieldVisitType = fieldVisitType,
        isCheckIn = isCheckIn,
        isCheckOut = isCheckOut,
        isDone = isDone
    )
}
