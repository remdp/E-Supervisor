package com.euromix.esupervisor.app.model.storeCheck.entities

import android.os.Parcelable
import com.euromix.esupervisor.app.model.common.entities.ServerObject
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

data class StoreCheckTopLevel(
    val isBasis: Boolean = false,
    val extId: String,
    val availabilityPos: Boolean = false,
    val pos: Boolean = false,
    val posBasis: Boolean = false,
    val isCheckOut: Boolean = false,
    val rows: List<StoreCheckTopLevelRow> = listOf()
)


data class StoreCheckTopLevelRow(
    val serverObject: ServerObject,
    val sales: List<StoreCheckSale>
)