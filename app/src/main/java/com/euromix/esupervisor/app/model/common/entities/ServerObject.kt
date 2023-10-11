package com.euromix.esupervisor.app.model.common.entities

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServerObject(
    @field:Json(name = "server_pair") val serverPair: ServerPair,
    @field:Json(name = "server_type") val serverType: String
): Parcelable
