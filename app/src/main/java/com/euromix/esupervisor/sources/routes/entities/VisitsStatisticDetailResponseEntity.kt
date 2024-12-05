package com.euromix.esupervisor.sources.routes.entities

import com.euromix.esupervisor.app.model.routes.entities.VisitsStatisticDetailData
import com.squareup.moshi.Json

data class VisitsStatisticDetailResponseEntity(
    val id: String,
    @field:Json(name = "avg_number_orders_20") val avgNumberOrders20: Int = 0,
    @field:Json(name = "avg_number_orders") val avgNumberOrders: Int = 0,
    @field:Json(name = "avg_amount_orders") val avgAmountOrders: Double = 0.0,
    @field:Json(name = "overdue_receivables") val overdueReceivables: Double = 0.0,
    @field:Json(name = "overdue_receivables_route") val overdueReceivablesRoute: Double = 0.0,
    @field:Json(name = "amount_payments") val amountPayments: Double = 0.0,
    @field:Json(name = "manufacturers_portfolio") val manufacturersPortfolio: Int = 0,
    @field:Json(name = "manufacturers_route") val manufacturersRoute: Int = 0,
    @field:Json(name = "manufacturers_logo") val manufacturersLogo: List<ManufacturerLogo>? = null
) {
    fun toVisitsStatisticDetail() = VisitsStatisticDetailData(
        id = id,
        avgNumberOrders20 = avgNumberOrders20,
        avgNumberOrders = avgNumberOrders,
        avgAmountOrders = avgAmountOrders,
        overdueReceivables = overdueReceivables,
        overdueReceivablesRoute = overdueReceivablesRoute,
        amountPayments = amountPayments,
        manufacturersPortfolio = manufacturersPortfolio,
        manufacturersRoute = manufacturersRoute,
        manufacturersLogo = manufacturersLogo?.chunked(MANUFACTURERS_IN_ITEM) ?: listOf()
    )

    companion object{
        const val MANUFACTURERS_IN_ITEM = 6
    }
}

data class ManufacturerLogo(
    val name: String,
    val url: String,
    @field:Json(name = "current_order") val currentOrder: Boolean,
    @field:Json(name = "number_orders") val numberOrders: Int,
    @field:Json(name = "orders_sum") val ordersSum: Double
)
