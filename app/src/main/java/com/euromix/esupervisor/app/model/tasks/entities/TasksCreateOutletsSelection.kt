package com.euromix.esupervisor.app.model.tasks.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TasksCreateOutletsSelection(
    val tradingAgents: List<String>,
    val outletsInnerTypes: List<String>? = null
) : Parcelable
