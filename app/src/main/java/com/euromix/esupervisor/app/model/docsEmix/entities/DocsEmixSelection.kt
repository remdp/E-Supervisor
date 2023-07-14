package com.euromix.esupervisor.app.model.docsEmix.entities

import android.os.Parcelable
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class DocsEmixSelection(
    val period: Pair<Date, Date>? = null,
    val tradingAgent: ServerPair? = null,
    val partner: ServerPair? = null,
    val operationType: ServerPair? = null,
    val status: ServerPair? = null
) : Parcelable {

    companion object {

        fun isEmpty(selection: DocsEmixSelection?) =
            selection == null || (selection.tradingAgent == null && selection.partner == null && selection.operationType == null && selection.status == null)
    }
}

//@Parcelize
//data class ServerPair(
//    val id: String,
//    val presentation: String
//) : Parcelable



