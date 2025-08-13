package com.euromix.esupervisor.sources.visitsSupervisors.entities

import android.os.Parcelable
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.CheckIn
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckInResponseEntity(
    @field:Json(name = "is_check_in") val isCheckIn: Boolean,
    val outlet: String,
    val longitude: Double,
    val latitude: Double,
    @field:Json(name = "check_in_photo") val checkInPhoto: String,
    @field:Json(name = "check_in_deviation") val checkInDeviation: Int,
    @field:Json(name = "check_in_by_photo") val checkInByPhoto: Boolean
) : Parcelable {
    fun toCheckIn() = CheckIn(
        isCheckIn = isCheckIn,
        outlet = outlet,
        longitude = longitude,
        latitude = latitude,
        checkInPhoto = checkInPhoto,
        checkInDeviation = checkInDeviation,
        checkInByPhoto = checkInByPhoto
    )
}
