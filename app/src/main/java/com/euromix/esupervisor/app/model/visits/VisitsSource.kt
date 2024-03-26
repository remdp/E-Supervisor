package com.euromix.esupervisor.app.model.visits

import com.euromix.esupervisor.app.model.visits.entities.ChangeVisitTypeReason
import com.euromix.esupervisor.app.model.visits.entities.Visit
import com.euromix.esupervisor.sources.visits.entities.VisitsChangeTypeRequestEntity
import com.euromix.esupervisor.sources.visits.entities.VisitsRequestEntity
interface VisitsSource {
    suspend fun getVisits(request: VisitsRequestEntity?): List<Visit>
    suspend fun getChangeTypeVisitReasons(): List<ChangeVisitTypeReason>
    suspend fun changeVisitsType(visitsChangeType: VisitsChangeTypeRequestEntity): String
}