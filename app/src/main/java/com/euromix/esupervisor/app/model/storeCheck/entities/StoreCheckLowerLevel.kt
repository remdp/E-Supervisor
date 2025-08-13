package com.euromix.esupervisor.app.model.storeCheck.entities

import com.euromix.esupervisor.app.enums.ShelfShare
import com.euromix.esupervisor.app.enums.StoreCheckFormat

data class StoreCheckLowerLevel(
    val isBasis: Boolean = false,
    val availabilityPos: Boolean = false,
    val pos: Boolean = false,
    val posBasis: Boolean = false,
    val storeCheckFormat: StoreCheckFormat? = null,
    val shelfShare: ShelfShare? = null,
    val newProducts: Boolean = false,
    val isCheckOut: Boolean = false,
    val number: String = "",
    val partner: String = "",
    val outlet: String = "",
    val tradeAgent: String = "",
    val tasksCount: Int = 0,
    val rows: List<StoreCheckLowerLevelRow> = listOf()
)

data class StoreCheckLowerLevelRow(
    val rowNumber: Int,
    val name: String,
    val imgUrl: String,
    val accountingBoolean: Boolean,
    val accountingInt: Int,
    val accountingBooleanBasis: Boolean,
    val accountingIntBasis: Int,
    val amountFacing: Int,
    val amountFacingBasis: Int,
    val amountFacingTotal: Int,
    val amountFacingTotalBasis: Int,
    val shelfLength: Int,
    val shelfLengthBasis: Int,
    val shelfLengthTotal: Int,
    val shelfLengthTotalBasis: Int,
    val newProducts: Int,
    val newProductsBasis: Int,
    val sales: List<StoreCheckSale>
) {
    companion object {
        const val ACCOUNTING_BOOLEAN = "accountingBoolean"
        const val ACCOUNTING_INT = "accountingInt"
        const val AMOUNT_FACING = "amountFacing"
        const val AMOUNT_FACING_TOTAL = "amountFacingTotal"
        const val SHELF_LENGTH = "shelfLength"
        const val SHELF_LENGTH_TOTAL = "shelfLengthTotal"
        const val NEW_PRODUCTS = "newProducts"
    }
}
