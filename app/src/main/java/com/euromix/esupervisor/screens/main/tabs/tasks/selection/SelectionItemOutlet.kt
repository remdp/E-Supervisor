package com.euromix.esupervisor.screens.main.tabs.tasks.selection

import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.tasks.entities.Outlet

data class SelectionItemOutlet(
    var marked: Boolean = false,
    val outlet: Outlet
)
