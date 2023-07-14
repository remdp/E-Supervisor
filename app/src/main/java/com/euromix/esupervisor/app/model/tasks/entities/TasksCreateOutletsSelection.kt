package com.euromix.esupervisor.app.model.tasks.entities

import android.os.Parcelable
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import kotlinx.parcelize.Parcelize

@Parcelize
data class TasksCreateOutletsSelection(
    val tradingAgents: List<ServerPair>,
    val outletsInnerTypes: List<ServerPair>? = null
) : Parcelable
