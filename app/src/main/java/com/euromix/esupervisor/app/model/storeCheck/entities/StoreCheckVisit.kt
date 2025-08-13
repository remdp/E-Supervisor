package com.euromix.esupervisor.app.model.storeCheck.entities

import com.euromix.esupervisor.app.model.tasks.entities.Task

data class StoreCheckVisit(
    val id: String,
    val number: String,
    val partner: String,
    val outlet: String,
    val tradeAgent: String,
    val twoLevels: Boolean,
    val tasks: List<Task>
)
