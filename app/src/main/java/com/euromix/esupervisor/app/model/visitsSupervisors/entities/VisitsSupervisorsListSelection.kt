package com.euromix.esupervisor.app.model.visitsSupervisors.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class VisitsSupervisorsListSelection(
    val period: Pair<LocalDate, LocalDate>? = null,
    val onlyMyVisits: Boolean = false,
    val supervisors: List<String> = listOf()
) : Parcelable
