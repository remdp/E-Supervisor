package com.euromix.esupervisor.app.enums

import com.euromix.esupervisor.App
import com.euromix.esupervisor.R

enum class Rate {

    SalesPlanFact {
        override fun nameStringsRes(): Int = R.string.sales_plan_fact
    },
    Coverage {
        override fun nameStringsRes(): Int = R.string.coverage
    },
    VisitsEfficiencyPlan {
        override fun nameStringsRes(): Int = R.string.visits_efficiency_plan
    },
    VisitsEfficiencyFact {
        override fun nameStringsRes(): Int = R.string.visits_efficiency_fact
    },
    OutletsWithOrders {
        override fun nameStringsRes(): Int = R.string.outlets_with_orders
    },
    TDP {
        override fun nameStringsRes(): Int = R.string.tdp
    },

    NumberOrders{
        override fun nameStringsRes() = R.string.number_orders
    },

    Undefined {
        override fun nameStringsRes() = R.string.undefined
    }
    ;

    abstract fun nameStringsRes(): Int

    companion object {
        fun allRates(): List<Rate> = listOf(
            SalesPlanFact,
            Coverage,
            VisitsEfficiencyPlan,
            VisitsEfficiencyFact,
            TDP,
            NumberOrders
        )
        fun getByIndex(index: Int): Rate {

            return if (index == -1) Undefined else {
                val rates = Rate.allRates()
                if (rates.size > index) rates[index] else Undefined
            }
        }
    }
}