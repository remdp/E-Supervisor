package com.euromix.esupervisor.sources.storeCheck.entities

import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckSale

data class StoreCheckResponseEntitySale(
    val product: String,
    val sum: Double,
    val imgUrl: String
) {
    fun toStoreCheckSale() = StoreCheckSale(product = product, sum = sum, imgUrl = imgUrl)
}
