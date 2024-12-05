package com.euromix.esupervisor.sources.visitsSupervisors.entities

import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitSupervisorDetail

data class VisitSupervisorDetailResponseEntity(
    val outlet: String,
    val partner: String,
    val longitude: Double,
    val latitude: Double
) {
    fun toVisitSupervisorDetail() = VisitSupervisorDetail(
        outlet = outlet,
        partner = partner,
        longitude = longitude,
        latitude = latitude
    )

}
