package com.euromix.esupervisor.app.enums

import com.euromix.esupervisor.R

enum class DocEmixOperationType {

    ChangeTC {
        override fun nameStringRes(): Int = R.string.change_tc
    },
    NewPartnerFact {
        override fun nameStringRes(): Int = R.string.new_partner_fact
    },
    Undefined {
        override fun nameStringRes(): Int = R.string.undefined
    };

    abstract fun nameStringRes(): Int

    companion object{

        fun getByIndex(index: Int): DocEmixOperationType {

            return when (index) {
                0 -> ChangeTC
                1 -> NewPartnerFact
                else -> Undefined
            }
        }
    }
}