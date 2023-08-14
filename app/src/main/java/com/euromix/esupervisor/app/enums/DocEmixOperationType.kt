package com.euromix.esupervisor.app.enums

import android.content.Context
import android.widget.TextView
import com.euromix.esupervisor.App
import com.euromix.esupervisor.App.Companion.getColor
import com.euromix.esupervisor.R

enum class DocEmixOperationType {
    ADD_TC {
        override fun nameStringRes(): Int = R.string.add_tc
    },
    NEW_PARTNER_FACT {
        override fun nameStringRes(): Int = R.string.new_partner_fact
    },
    RETURN_REQUEST {
        override fun nameStringRes(): Int = R.string.return_request
    },
    UNDEFINED {
        override fun nameStringRes(): Int = R.string.undefined
    };

    abstract fun nameStringRes(): Int

    companion object {

        fun stringRepresentation(context: Context, docEmixOperationType: DocEmixOperationType): String =
            App.getString(context, docEmixOperationType.nameStringRes())

        fun designTV(
            tvOperationType: TextView,
            operationType: DocEmixOperationType,
            status: Status,
            detail: Boolean = false
        ) {

            tvOperationType.text = stringRepresentation(tvOperationType.context, operationType)
            tvOperationType.background = getOperationTypeDrawable(tvOperationType.context, status, detail)
            tvOperationType.setTextColor(
                getColor(
                    tvOperationType.context,
                    getOperationTypeTextColor(status)
                )
            )
        }

        private fun getOperationTypeTextColor(status: Status) =
            when (status) {
                Status.IN_THE_PROCESS_OF_APPROVAL -> R.color.blue
                else -> R.color.gray_400
            }

        private fun getOperationTypeDrawable(context: Context, status: Status, detail: Boolean = false) =
            App.getDrawable(
                context, when (status) {
                    Status.IN_THE_PROCESS_OF_APPROVAL -> R.drawable.bg_operation_type_blue
                    else -> if (detail) R.drawable.bg_operation_type_white else R.drawable.bg_operation_type_gray
                }
            )

        fun operationTypes() = arrayOf(ADD_TC, NEW_PARTNER_FACT, RETURN_REQUEST)

        fun getByIndex(index: Int): DocEmixOperationType {

            return if (index == -1) UNDEFINED else {
                val operationTypes = operationTypes()
                if (operationTypes.size > index) operationTypes[index] else UNDEFINED
            }
        }
    }
}