package com.euromix.esupervisor.app.model.storeCheck.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoreCheckSale(
    val product: String,
    val sum: Double,
    val imgUrl: String
):Parcelable