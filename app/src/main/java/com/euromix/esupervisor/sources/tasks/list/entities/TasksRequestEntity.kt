package com.euromix.esupervisor.sources.tasks.list.entities

import com.squareup.moshi.Json

data class TasksRequestEntity(
    @field:Json(name = "start_date") val startDate: String? = null,
    @field:Json(name = "end_date") val endDate: String? = null,
   // @field:Json(name = "task_producer_id") val taskProducerId: String? = null,
    @field:Json(name = "partner_id") val partnerId: String? = null,
    @field:Json(name = "task_type_id") val taskTypeId: String? = null,
    @field:Json(name = "task_state_id") val taskStateId: String? = null,
    @field:Json(name = "executor_id") val executorId: String? = null,
    @field:Json(name = "only_my") val onlyMy: Boolean? = false,
    @field:Json(name = "only_overdue") val onlyOverdue: Boolean? = false
)
