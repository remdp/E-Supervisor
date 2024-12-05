package com.euromix.esupervisor.app.model.visitsSupervisors.entities

import java.time.LocalDateTime

data class VisitSupervisor(
    val id: String,
    val date: LocalDateTime,
    val number: String,
    val outlet: String,
    val partner: String
)
