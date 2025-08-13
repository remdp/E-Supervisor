package com.euromix.esupervisor.sources.storeCheck.entities

import com.euromix.esupervisor.app.model.common.entities.ServerObject
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckTopLevel
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckTopLevelRow
import com.squareup.moshi.Json

data class StoreCheckTopLevelResponseEntity(
    @field:Json(name = "is_basis") val isBasis: Boolean,
    val extId: String,
    @field:Json(name = "availability_pos") val availabilityPos: Boolean,
    val pos: Boolean,
    @field:Json(name = "pos_basis") val posBasis: Boolean,
    @field:Json(name = "is_check_out") val isCheckOut: Boolean,
    val rows: List<StoreCheckTopLevelResponseEntityRow>
) {
    fun toStoreCheckTopLevel() = StoreCheckTopLevel(
        isBasis = isBasis,
        extId = extId,

        availabilityPos = availabilityPos,
        pos = pos,
        posBasis = posBasis,
        isCheckOut = isCheckOut,
        rows = rows.map { it.toStoreCheckTopLevelRow() }
    )
}

data class StoreCheckTopLevelResponseEntityRow(
    val serverObject: ServerObject,
    val sales: List<StoreCheckResponseEntitySale>
) {
    fun toStoreCheckTopLevelRow() = StoreCheckTopLevelRow(
        serverObject = serverObject,
        sales = sales.map { it.toStoreCheckSale() })
}
