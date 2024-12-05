package com.euromix.esupervisor.sources.visitsSupervisors.entities

import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitSupervisor
import java.time.LocalDateTime

data class VisitsSupervisorResponseEntity(
    val id: String,
    val date: String,
    val number: String,
    val outlet: String,
    val partner: String
) {
    fun toVisitSupervisor() = VisitSupervisor(
        id = id,
        date = LocalDateTime.parse(date),
        number = number,
        outlet = outlet,
        partner = partner
    )

}
