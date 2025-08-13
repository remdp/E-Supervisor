package com.euromix.esupervisor.sources.visitsSupervisors.entities

import android.os.Parcelable
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitSupervisorDetail
import com.euromix.esupervisor.sources.tasks.list.entities.TasksResponseEntity
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class VisitSupervisorDetailResponseEntity(
    val outlet: String,
    val partner: String,
    @field:Json(name = "is_checkin") val isCheckIn: Boolean,
    @field:Json(name = "is_checkout") val isCheckOut: Boolean,
    @field:Json(name = "store_checks") val storeChecks: List<Row>,
    val tasks: List<TasksResponseEntity>

) : Parcelable {
    fun toVisitSupervisorDetail() = VisitSupervisorDetail(
        outlet = outlet,
        partner = partner,
        isCheckIn = isCheckIn,
        isCheckOut = isCheckOut,
        storeChecks = storeChecks.map { it.toVisitSupervisorDetailStoreCheckRow() },
        tasks = tasks.map { it.toTask() }
    )
}

@Parcelize
data class Row(
    val date: String,
    val number: String,
    val posted: Boolean,
    @field:Json(name = "levels_number") val levelsNumber: Int,
    @field:Json(name = "store_check") val storeCheck: ServerPair

) : Parcelable {
    fun toVisitSupervisorDetailStoreCheckRow() =
        com.euromix.esupervisor.app.model.visitsSupervisors.entities.StoreCheckRow(
            date = LocalDateTime.parse(date),
            number = number,
            posted = posted,
            levelsNumber = levelsNumber,
            storeCheck = storeCheck
        )
}
