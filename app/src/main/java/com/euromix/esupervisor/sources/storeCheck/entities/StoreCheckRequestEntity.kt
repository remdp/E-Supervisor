package com.euromix.esupervisor.sources.storeCheck.entities

import com.squareup.moshi.Json

data class StoreCheckRequestEntity(
    val pos: Boolean,
    val rows: List<Row>
)

data class Row(
    @field:Json(name = "row_number") val rowNumber: Int,
    @field:Json(name = "accounting_boolean") val accountingBoolean: Boolean,
    @field:Json(name = "accounting_int") val accountingInt: Int,
    @field:Json(name = "amount_facing") val amountFacing: Int,
    @field:Json(name = "amount_facing_total") val amountFacingTotal: Int,
    @field:Json(name = "shelf_length") val shelfLength: Int,
    @field:Json(name = "shelf_length_total") val shelfLengthTotal: Int,
    @field:Json(name = "new_products") val newProducts: Int
)
