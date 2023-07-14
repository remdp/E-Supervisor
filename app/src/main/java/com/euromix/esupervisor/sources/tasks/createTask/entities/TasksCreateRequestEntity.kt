package com.euromix.esupervisor.sources.tasks.createTask.entities

import com.squareup.moshi.Json

data class TasksCreateRequestEntity(
    @Json(name = "dead_line") val deadline: String,
    @Json(name = "task_type_id") val taskTypeId: String,
    @Json(name = "trading_agent_ids") val tradingAgentIds: List<String>,
    val description: String? = null,
    @Json(name = "outlets_ids") val outletsIds: List<String>
)
