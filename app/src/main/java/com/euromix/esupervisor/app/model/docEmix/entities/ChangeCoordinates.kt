package com.euromix.esupervisor.app.model.docEmix.entities

data class ChangeCoordinates(
    val date: String,
    val distance: Int,
    val outletPresentation: String,
    val latitudeOld: Double,
    val longitudeOld: Double,
    val latitudeNew: Double,
    val longitudeNew: Double,
    val canBeAgreed: Boolean
)
