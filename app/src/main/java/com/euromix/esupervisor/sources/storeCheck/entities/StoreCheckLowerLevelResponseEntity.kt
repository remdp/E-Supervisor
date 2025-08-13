package com.euromix.esupervisor.sources.storeCheck.entities

import com.euromix.esupervisor.app.enums.ShelfShare
import com.euromix.esupervisor.app.enums.StoreCheckFormat
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckLowerLevel
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckLowerLevelRow
import com.squareup.moshi.Json

data class StoreCheckLowerLevelResponseEntity(
    @field:Json(name = "is_basis") val isBasis: Boolean,
    @field:Json(name = "availability_pos") val availabilityPos: Boolean,
    val pos: Boolean,
    @field:Json(name = "pos_basis") val posBasis: Boolean,
    @field:Json(name = "store_check_format") val storeCheckFormat: Int,
    @field:Json(name = "shelf_share") val shelfShare: Int,
    @field:Json(name = "new_products") val newProducts: Boolean,
    @field:Json(name = "is_check_out") val isCheckOut: Boolean,
    val number: String,
    val partner: String,
    val outlet: String,
    @field:Json(name = "trade_agent") val tradeAgent: String,
    @field:Json(name = "tasks_count") val tasksCount: Int,
    val rows: List<StoreCheckLowerLevelResponseEntityRow>
) {
    fun toStoreCheckLowerLevel() = StoreCheckLowerLevel(
        isBasis = isBasis,
        availabilityPos = availabilityPos,
        pos = pos,
        posBasis = posBasis,
        storeCheckFormat = StoreCheckFormat.getByIndex(storeCheckFormat),
        shelfShare = ShelfShare.getByIndex(shelfShare),
        newProducts = newProducts,
        isCheckOut = isCheckOut,
        number = number,
        partner = partner,
        outlet = outlet,
        tradeAgent = tradeAgent,
        tasksCount = tasksCount,
        rows = rows.map { it.toStoreCheckLowerLevelRow() })
}

data class StoreCheckLowerLevelResponseEntityRow(
    @field:Json(name = "row_number") val rowNumber: Int,
    val name: String,
    @field:Json(name = "img_url") val imgUrl: String,
    @field:Json(name = "amount_facing") val amountFacing: Int,
    @field:Json(name = "amount_facing_basis") val amountFacingBasis: Int,
    @field:Json(name = "amount_facing_total") val amountFacingTotal: Int,
    @field:Json(name = "amount_facing_total_basis") val amountFacingTotalBasis: Int,
    @field:Json(name = "shelf_length") val shelfLength: Int,
    @field:Json(name = "shelf_length_basis") val shelfLengthBasis: Int,
    @field:Json(name = "shelf_length_total") val shelfLengthTotal: Int,
    @field:Json(name = "shelf_length_total_basis") val shelfLengthTotalBasis: Int,
    @field:Json(name = "new_products") val newProducts: Int,
    @field:Json(name = "new_products_basis") val newProductsBasis: Int,
    @field:Json(name = "accounting_boolean") val accountingBoolean: Boolean,
    @field:Json(name = "accounting_boolean_basis") val accountingBooleanBasis: Boolean,
    @field:Json(name = "accounting_int") val accountingInt: Int,
    @field:Json(name = "accounting_int_basis") val accountingIntBasis: Int,
    val sales: List<StoreCheckResponseEntitySale>
) {
    fun toStoreCheckLowerLevelRow() = StoreCheckLowerLevelRow(
        rowNumber = rowNumber,
        name = name,
        imgUrl = imgUrl,
        amountFacing = amountFacing,
        amountFacingBasis = amountFacingBasis,
        amountFacingTotal = amountFacingTotal,
        amountFacingTotalBasis = amountFacingTotalBasis,
        shelfLength = shelfLength,
        shelfLengthBasis = shelfLengthBasis,
        shelfLengthTotal = shelfLengthTotal,
        shelfLengthTotalBasis = shelfLengthTotalBasis,
        newProducts = newProducts,
        newProductsBasis = newProductsBasis,
        accountingBoolean = accountingBoolean,
        accountingInt = accountingInt,
        accountingBooleanBasis = accountingBooleanBasis,
        accountingIntBasis = accountingIntBasis,
        sales = sales.map { it.toStoreCheckSale() }
    )
}