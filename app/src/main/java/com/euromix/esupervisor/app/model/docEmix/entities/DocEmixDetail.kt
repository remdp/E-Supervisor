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
    val workingName: String?,
    val innerDistributionChannel: String?,
    val edrpou: String?,
    val outletName: String?,
    val institutionType: String?,
    val institutionNumber: String?,
    val generalOutlet: String?,
    val newGeneralOutlet: Boolean?,
    val outletFormat: String?,
    val locality: String?,
    val street: String?,
    val houseNumber: String?,
    val visitDays: Array<Boolean>?,
    val visitsFrequency: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val rowsTradeConditions: List<RowTradeCondition>?,
    val rowsReturnRequest: List<RowReturnRequest>?,
    val images: Int
) : Parcelable

@Parcelize
data class RowTradeCondition(
    val row: Int,
    val manufacturer: String,
    val priceIndex: Float,
    val paymentDeferment: Int,
    val distributionChannel: String,
    val imageURI: String
) : Parcelable

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
) : Parcelable