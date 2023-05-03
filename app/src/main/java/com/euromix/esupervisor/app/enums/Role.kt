package com.euromix.esupervisor.app.enums

import android.content.Context
import com.euromix.esupervisor.App
import com.euromix.esupervisor.App.Companion.getString
import com.euromix.esupervisor.R
import java.lang.Enum
import kotlin.Exception
import kotlin.Int
import kotlin.String

enum class Role {

    SUPERVISOR {
        override fun nameStringsRes(): Int = R.string.supervisor
    },
    HEAD_OF_THE_TRADING_DEPARTMENT {
        override fun nameStringsRes(): Int = R.string.head_t_d
    },
    DIRECTOR {
        override fun nameStringsRes(): Int = R.string.director
    };


    abstract fun nameStringsRes(): Int

    companion object {

        fun getByIndex(index: Int): Role =
            when (index) {
                1 -> HEAD_OF_THE_TRADING_DEPARTMENT
                2 -> DIRECTOR
                else -> SUPERVISOR
            }

        fun toRole(roleString: String?): Role {

            return if (roleString == null) SUPERVISOR
            else {
                try {
                    valueOf(roleString)
                } catch (ex: Exception) {
                    SUPERVISOR
                }
            }
        }

        fun getMinDetailLevel(role: Role?) =
            when(role){
                DIRECTOR -> 0
                HEAD_OF_THE_TRADING_DEPARTMENT -> 1
                else -> 2
            }

        fun stringRepresentation(context: Context, role: Role): String =
            getString(context, role.nameStringsRes())
    }

}