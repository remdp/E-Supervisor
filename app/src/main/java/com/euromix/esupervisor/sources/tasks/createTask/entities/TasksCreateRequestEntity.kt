package com.euromix.esupervisor.sources.tasks.createTask.entities

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class TasksCreateRequestEntity(
    @Json(name = "dead_line") val deadline: String,
    @Json(name = "task_type_id") val taskTypeId: String,
    @Json(name = "trading_agent_ids") val tradingAgentIds: List<String>? = null,
    val description: String? = null,
    @Json(name = "outlets_ids") val outletsIds: List<String>? = null,
    @Json(name = "attach_photo") val attachPhoto: Boolean,
    @Json(name = "store_check_id") val storeCheckId: String? = null,
    val photos: List<String>? = null
):Parcelable
