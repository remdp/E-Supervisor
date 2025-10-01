package com.euromix.esupervisor.sources.tasks.createTask.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskChangeRequestEntity(
    val id: String,
    val description: String
):Parcelable
