package com.euromix.esupervisor.app.model.docsEmix.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class DocsEmixSelection(
    val period: Pair<Date, Date>? = null,
    val tradingAgent: ItemSelection? = null,
    val partner: ItemSelection? = null,
    val operationType: ItemSelection? = null,
    val status: ItemSelection? = null
) : Parcelable {

    companion object {

        fun isEmpty(selection: DocsEmixSelection?) =
            selection == null || (selection.tradingAgent == null && selection.partner == null && selection.operationType == null && selection.status == null)
    }
}

@Parcelize
data class ItemSelection(
    val id: String,
    val presentation: String
) : Parcelable



