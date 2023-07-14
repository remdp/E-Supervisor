package com.euromix.esupervisor.sources.tasks.list.entities

import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.tasks.entities.Outlet

data class OutletsResponseEntity(
    val id: String,
    val name: String,
    val owner: String
) {

    fun toOutlet() = Outlet(owner = owner, serverPair = ServerPair(id, name))
}