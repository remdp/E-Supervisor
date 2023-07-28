package com.euromix.esupervisor.app.enums

import com.euromix.esupervisor.R

enum class RatesDetailing {

    BalanceUnit {
        override fun nameStringRes() = R.string.balance_unit

        override fun stringRepresentation(): String {
            TODO("Not yet implemented")
        }
    },
    TradingTeam {
        override fun nameStringRes() = R.string.trading_team

        override fun stringRepresentation(): String {
            TODO("Not yet implemented")
        }
    },
    TradingAgent {
        override fun nameStringRes() = R.string.trading_agent

        override fun stringRepresentation(): String {
            TODO("Not yet implemented")
        }
    },
    Manufacturer {
        override fun nameStringRes() = R.string.manufacturer

        override fun stringRepresentation(): String {
            TODO("Not yet implemented")
        }
    },
    Undefined {
        override fun nameStringRes() = R.string.undefined

        override fun stringRepresentation(): String {
            TODO("Not yet implemented")
        }

    };

    abstract fun nameStringRes(): Int
    abstract fun stringRepresentation(): String

    companion object {

        fun allLevels(rate: Rate? = null): List<RatesDetailing> {

            val list = mutableListOf(BalanceUnit, TradingTeam, TradingAgent)

            if (rate == Rate.SalesPlanFact || rate == null) list.add(Manufacturer)
            return list
        }

        fun getByIndex(index: Int): RatesDetailing {

            return if (index == -1) Undefined else {
                val ratesDetailing = allLevels()
                if (ratesDetailing.size > index) ratesDetailing[index] else Undefined
            }
        }
    }
}