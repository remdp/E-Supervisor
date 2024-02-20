package com.euromix.esupervisor.app.model.routes.entities

import android.os.Parcelable
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class RouteMapSelection(
    val day: LocalDate,
    val tradingAgent: ServerPair? = null,
    val tradingTeam: ServerPair? = null,

    val outletsWithVisits: Boolean = true,
    val outletsWithoutVisits: Boolean = false,

    val outletsTTNotTA: Boolean = false,
    val outletsNotRouteTTButInOtherTT: Boolean = false,
    val promisingOutlets: Boolean = false

) : Parcelable {

    companion object {

        fun isEmpty(selection: RouteMapSelection?) =
            selection == null || ((selection.tradingAgent == null) && (selection.tradingTeam == null))
    }
}



