package com.euromix.esupervisor.app.enums

import com.euromix.esupervisor.R

enum class Rate {

    SalesPlanFact {
        override fun nameStringsRes(): Int = R.string.sales_plan_fact
        override fun stringRepresentation(): String = Rate.SALE_NAME_REQUEST
    },
    Coverage {
        override fun nameStringsRes(): Int = R.string.coverage
        override fun stringRepresentation(): String = Rate.COVERAGE_NAME_REQUEST
    },
    VisitsEfficiencyPlan {
        override fun nameStringsRes(): Int = R.string.visits_efficiency_plan
        override fun stringRepresentation(): String = Rate.VISITS_EFFICIENCY_NAME_REQUEST
    },
    VisitsEfficiencyFact {
        override fun nameStringsRes(): Int = R.string.visits_efficiency_fact
        override fun stringRepresentation(): String = Rate.VISITS_EFFICIENCY_NAME_REQUEST
    },
    OutletsWithOrders {
        override fun nameStringsRes(): Int = R.string.outlets_with_orders
        override fun stringRepresentation(): String = Rate.OUTLETS_WITH_ORDERS_NAME_REQUEST
    };

    abstract fun nameStringsRes(): Int
    abstract fun stringRepresentation(): String


    companion object {
        fun allRates(): List<Rate> = listOf(
            SalesPlanFact,
            // OutletsWithOrders,
            Coverage,
            VisitsEfficiencyPlan,
            VisitsEfficiencyFact
        )

        fun manufacturerDetailRates(): List<Rate> = listOf(
            SalesPlanFact,
            Coverage
        )



        fun stringRepresentation(position: Int): String =
            allRates()[position].stringRepresentation()

        private const val COVERAGE_NAME_REQUEST = "coverage"
        private const val OUTLETS_WITH_ORDERS_NAME_REQUEST = "outlets_with_orders_rate"
        private const val SALE_NAME_REQUEST = "sale_rate"
        private const val VISITS_EFFICIENCY_NAME_REQUEST = "visits_efficiency"
    }
}