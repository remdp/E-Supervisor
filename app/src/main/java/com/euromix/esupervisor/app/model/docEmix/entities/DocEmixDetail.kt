package com.euromix.esupervisor.app.model.docEmix.entities

import android.os.Parcelable
import com.euromix.esupervisor.app.enums.DocEmixOperationType
import com.euromix.esupervisor.app.enums.Status
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class DocEmixDetail(
    val extId: String,
    val date: LocalDateTime,
    val number: String,
    val description: String,
    val tradingAgent: String,
    val operationType: DocEmixOperationType,
    val partner: String,
    val status: Status,
    val canBeAgreed: Boolean,
    val sum: Float?,
    //new partner data
    val workingName: String?,
    val innerDistributionChannel: String?,
    val edrpou: String?,
    //new outlet data
    val outletName: String?,
    val institutionType: String?,
    val institutionNumber: String?,
    val generalOutlet: String?,
    val newGeneralOutlet: Boolean?,
    val outletFormat: String?,
    val locality: String?,
    val street: String?,
    val houseNumber: String?,
    val visitDay1:Boolean,
    val visitDay2:Boolean,
    val visitDay3:Boolean,
    val visitDay4:Boolean,
    val visitDay5:Boolean,
    val visitDay6:Boolean,
    val visitDay7:Boolean,
    val visitsFrequency: String,
    val rowsTradeConditions: List<RowTradeCondition>?,
    val rowsReturnRequest: List<RowReturnRequest>?,
    val imagesPaths: List<ImagesPaths>?
):Parcelable

@Parcelize
data class RowTradeCondition(
    val row: Int,
    val manufacturer: String,
    val priceIndex: Int,
    val paymentDeferment: Int,
    val distributionChannel: String,
    val imageURI: String
):Parcelable

@Parcelize
data class RowReturnRequest(
    val row: Int,
    val goods: String,
    val series: String,
    val amount: Float,
    val unit: String,
    val price: Float,
    val sum: Float,
    val base: String,
    val baseDate: String,
    val imageURI: String
):Parcelable

@Parcelize
data class ImagesPaths(
    val path: String
): Parcelable