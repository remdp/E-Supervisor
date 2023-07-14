package com.euromix.esupervisor.screens.main.tabs.tasks.selection

import com.euromix.esupervisor.app.model.common.entities.ServerPair

data class SelectionItem(
    var marked: Boolean = false,
    val serverPair: ServerPair
)
