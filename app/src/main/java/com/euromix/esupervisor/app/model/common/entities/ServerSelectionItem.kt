package com.euromix.esupervisor.app.model.common.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServerSelectionItem(
    val id: String, val serverType: String
): Parcelable
