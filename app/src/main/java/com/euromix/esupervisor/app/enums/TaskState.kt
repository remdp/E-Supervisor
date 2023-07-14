package com.euromix.esupervisor.app.enums

import android.content.Context
import android.widget.TextView
import com.euromix.esupervisor.App
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.utils.setIconTaskState
import com.euromix.esupervisor.app.utils.setTextColorTaskState

enum class TaskState {

    DRAFT {
        override fun getColor() = R.color.gray_400
        override fun getIconId() = R.drawable.ic_refresh_gray_300
        override fun nameStringRes() = R.string.draft
    },
    IN_WORK {
        override fun getColor(): Int = R.color.colorSelectiveYellow
        override fun getIconId(): Int = R.drawable.ic_refresh
        override fun nameStringRes() = R.string.in_work
    },
    DONE {
        override fun getColor(): Int = R.color.green
        override fun getIconId(): Int = R.drawable.ic_done
        override fun nameStringRes() = R.string.done
    },
    CANCEL {
        override fun getColor(): Int = R.color.red
        override fun getIconId(): Int = R.drawable.ic_reject
        override fun nameStringRes() = R.string.cancel
    },
    UNDEFINED {
        override fun getColor(): Int = R.color.colorCasper
        override fun getIconId(): Int = R.drawable.ic_baseline_question_mark_24
        override fun nameStringRes(): Int = R.string.undefined
    };

    abstract fun getColor(): Int
    abstract fun getIconId(): Int
    abstract fun nameStringRes(): Int

    companion object {

        fun stringRepresentation(context: Context, taskState: TaskState): String =
            App.getString(context, taskState.nameStringRes())


        fun designTV(tvTaskState: TextView, taskState: TaskState) {
            tvTaskState.text = stringRepresentation(tvTaskState.context, taskState)
            tvTaskState.setTextColorTaskState(taskState)
            tvTaskState.setIconTaskState(taskState)
        }

        fun taskStates() = arrayOf(DRAFT,IN_WORK, DONE, CANCEL, UNDEFINED)

        fun getByIndex(index: Int): TaskState {

            return if (index == -1) UNDEFINED else {
                val taskStates = taskStates()
                if (taskStates.size > index) taskStates[index] else UNDEFINED
            }
        }
    }
}