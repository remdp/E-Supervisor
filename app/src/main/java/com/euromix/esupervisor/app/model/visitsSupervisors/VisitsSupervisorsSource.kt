package com.euromix.esupervisor.app.model.visitsSupervisors

import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitSupervisor
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitSupervisorDetail
import com.euromix.esupervisor.sources.visitsSupervisors.entities.CheckInRequestEntity

interface VisitsSupervisorsSource {
    suspend fun getVisitsSupervisors(): List<VisitSupervisor>
    suspend fun getVisitSupervisorDetail(id: String): VisitSupervisorDetail
    suspend fun checkIn(id: String, request: CheckInRequestEntity)
}