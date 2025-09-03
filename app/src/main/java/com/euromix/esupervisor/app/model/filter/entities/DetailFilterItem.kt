package com.euromix.esupervisor.app.model.filter.entities

import android.os.Parcelable
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailFilterItem(
    val marked: Boolean = false,
    val serverPair: ServerPair
):Parcelable
