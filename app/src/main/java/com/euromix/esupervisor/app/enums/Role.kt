package com.euromix.esupervisor.app.enums

import com.euromix.esupervisor.R

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
    }

}