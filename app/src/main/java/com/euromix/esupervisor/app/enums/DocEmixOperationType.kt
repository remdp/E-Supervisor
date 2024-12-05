package com.euromix.esupervisor.app.enums

import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.euromix.esupervisor.R

enum class DocEmixOperationType {
    ADD_TC {
        override fun nameStringRes() = R.string.add_tc
    },
    NEW_PARTNER_FACT {
        override fun nameStringRes() = R.string.new_partner_fact
    },
    RETURN_REQUEST {
        override fun nameStringRes() = R.string.return_request
    },
    CHANGE_COORDINATES {
        override fun nameStringRes() = R.string.change_coordinates
    },
    UNDEFINED {
        override fun nameStringRes() = R.string.undefined
    };

    abstract fun nameStringRes(): Int

    companion object {

        fun designTV(
            tvOperationType: TextView,
            operationType: DocEmixOperationType,
            status: Status,
            detail: Boolean = false
        ) {

            tvOperationType.text = tvOperationType.context.getString(operationType.nameStringRes())
            tvOperationType.background = AppCompatResources.getDrawable(
                tvOperationType.context,
                when (status) {
                    Status.IN_THE_PROCESS_OF_APPROVAL -> R.drawable.bg_4dp_blue_40_border_gray_200
                    else -> if (detail) R.drawable.bg_4dp_white_border_gray_200 else R.drawable.bg_4dp_gray_100_border_gray_200
                }
            )

            tvOperationType.setTextColor(
                tvOperationType.context.getColor(
                    getOperationTypeTextColor(status)
                )
            )
        }

        private fun getOperationTypeTextColor(status: Status) =
            when (status) {
                Status.IN_THE_PROCESS_OF_APPROVAL -> R.color.blue
                else -> R.color.gray_400
            }

        fun operationTypes() = arrayOf(ADD_TC, NEW_PARTNER_FACT, RETURN_REQUEST, CHANGE_COORDINATES)

        fun getByIndex(index: Int): DocEmixOperationType {

            return if (index == -1) UNDEFINED else {
                val operationTypes = operationTypes()
                if (operationTypes.size > index) operationTypes[index] else UNDEFINED
            }
        }
    }
}