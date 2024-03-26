package com.euromix.esupervisor.app.model.visits.entities

import android.os.Parcelable
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class VisitsListSelection(
    val period: Pair<Date, Date>? = null,
    val tradingAgent: ServerPair? = null
) : Parcelable {

    companion object {

        fun isEmpty(selection: VisitsListSelection?) =
            selection == null || (selection.tradingAgent == null)
    }
}
