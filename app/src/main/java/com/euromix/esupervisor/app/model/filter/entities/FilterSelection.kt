package com.euromix.esupervisor.app.model.filter.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterSelection(
    val flags: List<FlagItem>,
    val items: List<FilterItem>
) : Parcelable {
    fun isFilterClear() = !flags.any { it.flag } && !items.any { filterItem ->
        filterItem.detailFilterItems.any { detailItem ->
            detailItem.marked
        }
    }
}
