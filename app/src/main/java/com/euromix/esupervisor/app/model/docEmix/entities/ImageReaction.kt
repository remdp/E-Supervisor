package com.euromix.esupervisor.app.model.docEmix.entities

data class ImageReaction(
    val id: Long,
    val user: String,
    val reaction: Int,
    val comment: String
)
