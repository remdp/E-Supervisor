package com.euromix.esupervisor.sources.storeCheck.entities

import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckVisit
import com.euromix.esupervisor.sources.tasks.list.entities.TasksResponseEntity
import com.squareup.moshi.Json

data class StoreCheckVisitResponseEntity(
    val id: String,
    val number: String,
    val partner: String,
    val outlet: String,
    @field:Json(name = "trade_agent") val tradeAgent: String,
    @field:Json(name = "two_levels") val twoLevels: Boolean,
    val tasks: List<TasksResponseEntity> = emptyList()
) {
    fun toStoreCheckVisit() =
        StoreCheckVisit(
            id = id,
            number = number,
            partner = partner,
            outlet = outlet,
            tradeAgent = tradeAgent,
            twoLevels = twoLevels,
            tasks = tasks.map { it.toTask() })
}
