package com.euromix.esupervisor.sources.visitsSupervisors.entities

import android.os.Parcelable
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.CheckOut
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckOutResponseEntity(
    @field:Json(name = "is_check_in") val isCheckIn: Boolean,
    @field:Json(name = "is_check_out") val isCheckOut: Boolean,
    val outlet: String,
    val longitude: Double,
    val latitude: Double,
    @field:Json(name = "outlet_time") val outletTime: Int
) : Parcelable {
    fun toCheckOut() = CheckOut(
        isCheckOut = isCheckOut,
        outlet = outlet,
        longitude = longitude,
        latitude = latitude,
        outletTime = outletTime
    )
}
