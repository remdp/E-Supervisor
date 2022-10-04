package com.euromix.esupervisor.sources.docsEmixDetail.entities

import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.model.docEmix.entities.RowTradeCondition
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

data class DocEmixDetailResponseEntity(
    val extId: String,
    val date: String,
    val number: String,
    val description: String,
    @field:Json(name = "trading_agent") val tradingAgent: String,
    @field:Json(name = "operation_type") val operationType: String,
    val partner: String,
    val status: String,
    @field:Json(name = "can_be_agreed") val canBeAgreed: Boolean,
    @field:Json(name = "trade_conditions_list") val tradeConditionResponseEntities: List<RowTradeConditionResponseEntity>?
) {
    fun toDocEmixDetail(): DocEmixDetail = DocEmixDetail(
        extId = extId,
        date = LocalDateTime.parse(date),
        number = number,
        description = description,
        tradingAgent = tradingAgent,
        operationType = operationType,
        partner = partner,
        status = status,
        canBeAgreed = canBeAgreed,
        rowTradeConditions = toRowTradeCondition()
    )

    private fun toRowTradeCondition(): List<RowTradeCondition> {

        val tradeConditionList: MutableList<RowTradeCondition> = mutableListOf()

        tradeConditionResponseEntities?.forEach {
            tradeConditionList.add(
                RowTradeCondition(
                    row = it.row,
                    manufacturer = it.manufacturer,
                    priceIndex = it.priceIndex,
                    paymentDeferment = it.paymentDeferment,
                    distributionChannel = it.distributionChannel
                )
            )
        }
        return tradeConditionList
    }
}

data class RowTradeConditionResponseEntity(
    val row: Int,
    val manufacturer: String,
    @field:Json(name = "price_index") val priceIndex: Int,
    @field:Json(name = "payment_deferment") val paymentDeferment: Int,
    @field:Json(name = "distribution_channel") val distributionChannel: String
)