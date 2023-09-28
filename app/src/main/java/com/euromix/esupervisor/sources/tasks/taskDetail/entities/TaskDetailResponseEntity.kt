package com.euromix.esupervisor.sources.tasks.taskDetail.entities

import com.euromix.esupervisor.app.enums.TaskState
import com.euromix.esupervisor.app.model.taskDetail.entities.TaskDetail
import com.squareup.moshi.Json
import java.time.LocalDateTime

data class TaskDetailResponseEntity(
    val id: String,
    val date: String,
    val number: String,
    @field:Json(name = "task_type") val taskType: String,
    val executor: String,
    val deadline: String,
    @field:Json(name = "task_state") val taskState: Int = -1,
    val description: String,
    @field:Json(name = "attach_photo") val attachPhoto: Boolean,
    val partner: String = "",
    val outlet: String = ""
) {
    fun toTaskDetail() = TaskDetail(
        id = id,
        date = LocalDateTime.parse(date),
        number = number,
        taskType = taskType,
        executor = executor,
        deadline = LocalDateTime.parse(deadline).toLocalDate(),
        taskState = TaskState.getByIndex(taskState),
        description = description,
        attachPhoto = attachPhoto,
        partner = partner,
        outlet = outlet
    )
}
