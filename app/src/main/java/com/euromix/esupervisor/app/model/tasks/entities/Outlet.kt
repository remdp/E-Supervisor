package com.euromix.esupervisor.app.model.tasks.entities

import com.euromix.esupervisor.app.model.common.entities.ServerPair

data class Outlet(
    val owner: String,
    val serverPair: ServerPair
)


