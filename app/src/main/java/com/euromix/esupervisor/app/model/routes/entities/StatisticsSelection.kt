package com.euromix.esupervisor.app.model.routes.entities

import android.os.Parcelable
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class StatisticsSelection(
    val period: Pair<Date, Date>,
    val detailLevel: Int = 0,
    val balanceUnit: ServerPair? = null,
    val tradingTeam: ServerPair? = null,
    val tradingAgent: ServerPair? = null
) : Parcelable {
    fun isEmpty(countUnclearedItems: Int) = when (countUnclearedItems) {
        2 -> tradingAgent == null
        1 -> tradingTeam == null && tradingAgent == null
        else -> balanceUnit == null && tradingTeam == null && tradingAgent == null
    }
}
