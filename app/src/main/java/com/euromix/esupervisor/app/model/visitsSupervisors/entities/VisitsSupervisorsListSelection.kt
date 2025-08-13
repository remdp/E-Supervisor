package com.euromix.esupervisor.app.model.visitsSupervisors.entities

import android.os.Parcelable
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.util.Date

@Parcelize
data class VisitsSupervisorsListSelection(
    val period: Pair<LocalDate, LocalDate>? = null
) : Parcelable {

    companion object {
        fun isEmpty(selection: VisitsSupervisorsListSelection?) = selection == null
    }
}
