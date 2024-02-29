package com.euromix.esupervisor.sources.docsEmixDetail.entities

import com.euromix.esupervisor.app.model.docEmix.entities.ChangeCoordinates
import com.squareup.moshi.Json
data class ChangeCoordinatesResponseEntity(
    val date: String,
    val distance: Int,
    @field:Json(name = "outlet_presentation") val outletPresentation: String,
    val latitudeOld: Double,
    val longitudeOld: Double,
    val latitudeNew: Double,
    val longitudeNew: Double,
    @field:Json(name = "can_be_agreed") val canBeAgreed: Boolean
) {
    fun toChangeCoordinates() = ChangeCoordinates(
        date = date,
        distance = distance,
        outletPresentation = outletPresentation,
        latitudeOld = latitudeOld,
        longitudeOld = longitudeOld,
        latitudeNew = latitudeNew,
        longitudeNew = longitudeNew,
        canBeAgreed = canBeAgreed
    )
}
