package com.euromix.esupervisor.app.model.docEmix.entities

import android.os.Parcelable
import com.euromix.esupervisor.app.enums.DocEmixOperationType
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
    val status: String,
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
    val rowsTradeConditions: List<RowTradeCondition>?,
    val rowsReturnRequest: List<RowReturnRequest>?,
    val picturesPaths: List<PicturesPaths>?
):Parcelable


@Parcelize
data class RowTradeCondition(
    val row: Int,
    val manufacturer: String,
    val priceIndex: Int,
    val paymentDeferment: Int,
    val distributionChannel: String
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
    val base: String
):Parcelable

@Parcelize
data class PicturesPaths(
    val path: String
): Parcelable