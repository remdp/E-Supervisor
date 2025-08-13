package com.euromix.esupervisor.app.enums

import com.euromix.esupervisor.app.enums.Status.Companion.statuses
import com.euromix.esupervisor.app.enums.Status.UNDEFINED

enum class StoreCheckFormat {

    AMOUNT_SKU,
    YES_NO,
    UNDEFINED;

    fun getIndex() = if (this == UNDEFINED) -1 else entries.indexOf(this)

    companion object {

        fun getByIndex(index: Int) = entries.getOrNull(index) ?: UNDEFINED

    }
}