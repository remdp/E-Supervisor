package com.euromix.esupervisor.app.model.tasks.entities

import android.os.Parcelable
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class TasksSelection(
    val period: Pair<Date, Date>? = null,
    // val taskProducer: ServerPair? = null,
    val partner: ServerPair? = null,
    val taskType: ServerPair? = null,
    val executor: ServerPair? = null,
    val taskState: ServerPair? = null,
    val onlyMy: Boolean? = false,
    val onlyOverdue: Boolean? = false
) : Parcelable {

    companion object {

        fun isEmpty(selection: TasksSelection?) =
            selection == null || (selection.partner == null &&
                    selection.taskType == null && selection.taskState == null &&
                    selection.executor == null && selection.onlyMy == false && selection.onlyOverdue == false)
    }
}
