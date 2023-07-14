package com.euromix.esupervisor.sources.tasks.createTask.entities

import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.tasks.entities.Outlet
import com.euromix.esupervisor.screens.main.tabs.tasks.selection.SelectionItemOutlet
import com.squareup.moshi.Json

data class OutletsForCreateTaskResponseEntity(
    val id: String,
    val presentation: String,
    val owner: String
){
    fun toSelectionItemOutlet() = SelectionItemOutlet(
        marked = false,
        outlet = Outlet(owner, ServerPair(id, presentation))
    )
}
