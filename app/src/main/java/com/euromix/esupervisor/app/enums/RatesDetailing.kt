package com.euromix.esupervisor.app.enums

import com.euromix.esupervisor.R

enum class RatesDetailing {

    BalanceUnit {
        override fun nameStringRes() = R.string.balance_unit
    },
    TradingTeam {
        override fun nameStringRes() = R.string.trading_team
    },
    Route {
        override fun nameStringRes() = R.string.route
    },
    Manufacturer {
        override fun nameStringRes() = R.string.manufacturer
    },
    Undefined {
        override fun nameStringRes() = R.string.undefined
    };

    abstract fun nameStringRes(): Int

    companion object {

        fun allLevels(rate: Rate? = null): List<RatesDetailing> {

            val list = mutableListOf(BalanceUnit, TradingTeam, Route)

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