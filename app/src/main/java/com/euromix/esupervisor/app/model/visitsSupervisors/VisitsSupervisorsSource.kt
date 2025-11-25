package com.euromix.esupervisor.app.model.visitsSupervisors

import com.euromix.esupervisor.app.model.visitsSupervisors.entities.CheckIn
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.CheckOut
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitSupervisor
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitSupervisorDetail
import com.euromix.esupervisor.sources.visitsSupervisors.entities.CheckInRequestEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.CheckOutRequestEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.RepeatStoreCheckRequestEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.TransferStoreCheckRequestEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.VisitsSupervisorsRequestEntity

interface VisitsSupervisorsSource {
    suspend fun getVisitsSupervisors(request: VisitsSupervisorsRequestEntity?): List<VisitSupervisor>
    suspend fun getVisitSupervisorDetail(id: String): VisitSupervisorDetail
    suspend fun checkInPost(id: String, request: CheckInRequestEntity): CheckIn
    suspend fun checkInGet(id: String): CheckIn
    suspend fun checkOutPost(id: String, request: CheckOutRequestEntity): CheckOut
    suspend fun checkOutGet(id: String): CheckOut
    suspend fun createRepeatStoreCheck(request: RepeatStoreCheckRequestEntity): String
    suspend fun transferStoreCheck(request: TransferStoreCheckRequestEntity): String
}