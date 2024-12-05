package com.euromix.esupervisor.app.model.routes.entities

import com.euromix.esupervisor.sources.routes.entities.ManufacturerLogo

data class VisitsStatisticDetailData(
    val id: String,
    val avgNumberOrders20: Int = 0,
    val avgNumberOrders: Int = 0,
    val avgAmountOrders: Double = 0.0,
    val overdueReceivables: Double = 0.0,
    val overdueReceivablesRoute: Double = 0.0,
    val amountPayments: Double = 0.0,
    val manufacturersPortfolio: Int = 0,
    val manufacturersRoute: Int = 0,
    val manufacturersLogo: List<List<ManufacturerLogo>> = listOf(),
    val watchAllManufacturersLogo: Boolean = false,
    val animateCharts: Boolean = true
)
