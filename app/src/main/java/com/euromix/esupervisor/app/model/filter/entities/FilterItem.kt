package com.euromix.esupervisor.app.model.filter.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterItem(
    val id: Int,
    val searchString: String = "",
    val detailFilterItems: List<DetailFilterItem>
):Parcelable
