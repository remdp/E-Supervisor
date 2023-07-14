package com.euromix.esupervisor.app.model.taskDetail.entities

import com.euromix.esupervisor.app.enums.TaskState
import java.time.LocalDate
import java.time.LocalDateTime

data class TaskDetail(
    val date: LocalDateTime,
    val number: String,
    val taskType: String,
    val executor: String,
    val deadline: LocalDate,
    val taskState: TaskState,
    val description: String,
    val attachPhoto: Boolean,
    val partner: String,
    val outlet: String
)
