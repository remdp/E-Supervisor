package com.euromix.esupervisor.app.model.visitsSupervisors.entities

import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.tasks.entities.Task
import java.time.LocalDateTime

data class VisitSupervisorDetail(
    val outlet: String,
    val partner: String,
    val isCheckIn: Boolean,
    val isCheckOut: Boolean,
    val storeChecks: List<StoreCheckRow>,
    val tasks: List<Task>
)

data class StoreCheckRow(
    val date: LocalDateTime,
    val number: String,
    val posted: Boolean,
    val levelsNumber: Int,
    val storeCheck: ServerPair
)

