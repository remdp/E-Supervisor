package com.euromix.esupervisor.app.enums

import android.content.Context
import android.widget.TextView
import com.euromix.esupervisor.App.Companion.getDrawable
import com.euromix.esupervisor.App.Companion.getString
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.utils.setIconStatus
import com.euromix.esupervisor.app.utils.setNonStandardStatusText
import com.euromix.esupervisor.app.utils.setTextColorStatus

enum class Status {

    DONE {
        override fun getColor(): Int = R.color.green
        override fun getIconId(): Int = R.drawable.ic_done
        override fun nameStringRes(): Int = R.string.done
    },
    IN_THE_PROCESS_OF_APPROVAL {
        override fun getColor(): Int = R.color.orange
        override fun getIconId(): Int = R.drawable.ic_refresh
        override fun nameStringRes(): Int = R.string.in_the_process_of_approval
    },
    AGREED {
        override fun getColor(): Int = R.color.colorAtlantis
        override fun getIconId(): Int = R.drawable.ic_agreed
        override fun nameStringRes(): Int = R.string.agreed
    },
    ERROR {
        override fun getColor(): Int = R.color.colorPomegranate
        override fun getIconId(): Int = R.drawable.ic_error
        override fun nameStringRes(): Int = R.string.error
    },
    REJECTED {
        override fun getColor(): Int = R.color.red
        override fun getIconId(): Int = R.drawable.ic_reject
        override fun nameStringRes(): Int = R.string.rejected
    },
    IN_WORK {
        override fun getColor(): Int = R.color.colorSelectiveYellow
        override fun getIconId(): Int = R.drawable.ic_in_work
        override fun nameStringRes(): Int = R.string.in_work
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

        fun designTV(tvStatus: TextView, status: Status) {
            tvStatus.setNonStandardStatusText(
                stringRepresentation(
                    tvStatus.context,
                    status
                )
            )
            tvStatus.setTextColorStatus(status)
            tvStatus.setIconStatus(status)
        }

        fun stringRepresentation(context: Context, status: Status): String =
            getString(context, status.nameStringRes())

        fun statuses() = arrayOf(DONE, IN_THE_PROCESS_OF_APPROVAL, AGREED, ERROR, REJECTED, IN_WORK)

        fun getByIndex(index: Int): Status {

            return if (index == -1) UNDEFINED else {
                val statuses = statuses()
                if (statuses.size > index) statuses[index] else UNDEFINED
            }

        }
    }
}