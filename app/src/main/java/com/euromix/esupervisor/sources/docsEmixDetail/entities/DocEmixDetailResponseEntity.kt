package com.euromix.esupervisor.sources.docsEmixDetail.entities

import com.euromix.esupervisor.app.enums.DocEmixOperationType
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.model.docEmix.entities.PicturesPaths
import com.euromix.esupervisor.app.model.docEmix.entities.RowReturnRequest
import com.euromix.esupervisor.app.model.docEmix.entities.RowTradeCondition
import com.squareup.moshi.Json
import java.time.LocalDateTime

data class DocEmixDetailResponseEntity(
    val extId: String,
    val date: String,
    val number: String,
    val description: String,
    @field:Json(name = "trading_agent") val tradingAgent: String,
    @field:Json(name = "operation_type") val operationType: Int,
    val partner: String,
    val status: String,
    @field:Json(name = "can_be_agreed") val canBeAgreed: Boolean,
    val sum: Float?,
    //new partner data
    @field:Json(name = "working_name") val workingName: String,
    @field:Json(name = "inner_distribution_channel") val innerDistributionChannel: String,
    val edrpou: String,
    //new outlet data
    @field:Json(name = "outlet_name") val outletName: String,
    @field:Json(name = "institution_type") val institutionType: String,
    @field:Json(name = "institution_number") val institutionNumber: String,
    @field:Json(name = "general_outlet") val generalOutlet: String,
    @field:Json(name = "new_general_outlet") val newGeneralOutlet: Boolean,
    @field:Json(name = "outlet_format") val outletFormat: String,
    val locality: String,
    val street: String,
    @field:Json(name = "house_number") val houseNumber: String,
    @field:Json(name = "trade_conditions_list") val tradeConditionResponseEntities: List<RowTradeConditionResponseEntity>?,
    @field:Json(name = "return_request_list") val returnRequestResponseEntities: List<RowReturnRequestResponseEntity>?,
    @field:Json(name = "pictures_paths") val picturesPaths: List<PicturesPaths>?

) {
    fun toDocEmixDetail(): DocEmixDetail = DocEmixDetail(
        extId = extId,
        date = LocalDateTime.parse(date),
        number = number,
        description = description,
        tradingAgent = tradingAgent,
        operationType = DocEmixOperationType.getByIndex(operationType),
        partner = partner,
        status = status,
        canBeAgreed = canBeAgreed,
        sum = sum,
        workingName = workingName,
        innerDistributionChannel = innerDistributionChannel,
        edrpou = edrpou,
        outletName = outletName,
        institutionType = institutionType,
        institutionNumber = institutionNumber,
        generalOutlet = generalOutlet,
        newGeneralOutlet = newGeneralOutlet,
        outletFormat = outletFormat,
        locality = locality,
        street = street,
        houseNumber = houseNumber,
        rowsTradeConditions = toRowTradeCondition(),
        rowsReturnRequest = toRowReturnRequest(),
        picturesPaths = picturesPaths
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

    private fun toRowReturnRequest(): List<RowReturnRequest>{

        val returnRequestList: MutableList<RowReturnRequest> = mutableListOf()

        returnRequestResponseEntities?.forEach {
            returnRequestList.add(
                RowReturnRequest(
                    row = it.row,
                    goods = it.goods,
                    series = it.series,
                    amount = it.amount,
                    unit = it.unit,
                    price = it.price,
                    sum = it.sum,
                    base = it.base
                )
            )
        }
        return returnRequestList
    }
}

data class RowTradeConditionResponseEntity(
    val row: Int,
    val manufacturer: String,
    @field:Json(name = "price_index") val priceIndex: Int,
    @field:Json(name = "payment_deferment") val paymentDeferment: Int,
    @field:Json(name = "distribution_channel") val distributionChannel: String
)

data class RowReturnRequestResponseEntity(
    val row: Int,
    val goods: String,
    val series: String,
    val amount: Float,
    val unit: String,
    val price: Float,
    val sum: Float,
    val base: String
)