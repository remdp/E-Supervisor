package com.euromix.esupervisor.app.model.visitsSupervisors.entities

import java.time.LocalDateTime

data class VisitSupervisor(
    val extId: String,
    val date: LocalDateTime,
    val number: String,
    val outlet: String,
    val partner: String,
    val outletVerified: Boolean,
    val fieldVisitType: Int,
    val isCheckIn: Boolean,
    val isCheckOut: Boolean,
    val isDone: Boolean,
    val showMark: Boolean = false,
    val mark: Boolean = false,
    val canBeRepeated: Boolean
)
