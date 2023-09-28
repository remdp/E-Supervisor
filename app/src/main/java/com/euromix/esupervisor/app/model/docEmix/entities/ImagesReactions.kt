package com.euromix.esupervisor.app.model.docEmix.entities

data class ImagesReactions(
    val creationDislikeTaskMessage: String,
    val rows: List<ImageReactionsRow>
)

data class ImageReactionsRow(
    val path: String,
    val likes: Int,
    var dislikes: Int,
    val comment: String?
)