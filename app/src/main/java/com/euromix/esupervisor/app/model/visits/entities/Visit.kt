package com.euromix.esupervisor.app.model.visits.entities

import com.euromix.esupervisor.app.enums.VisitType
import java.time.LocalDateTime

data class Visit(
    val extId:  String,
    val date: LocalDateTime,
    val number: String,
    val partner: String,
    val address: String,
    val checkIn: String,
    val outletTime: String,
    val orderSum: Float,
    val cashReceiptOrderSum: Float,
    val done: Boolean,
    val type: VisitType,
    val unscheduled: Boolean,
    val showMark: Boolean = false,
    val mark: Boolean = false,
    val checkInBounds: Boolean,
    val canBeChanged: Boolean
)
