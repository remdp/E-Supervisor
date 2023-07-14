package com.euromix.esupervisor.app.model.common.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServerPair(
    val id: String,
    val presentation: String
) : Parcelable
