package com.euromix.esupervisor.sources.docsEmixDetail.entities

import com.euromix.esupervisor.app.model.docEmix.entities.ImagesReactions
import com.euromix.esupervisor.app.model.docEmix.entities.ImageReactionsRow

data class ImagesReactionsResponseEntity(
    val creationDislikeTaskMessage: String, val rows: List<ImageReactionResponseEntityRow>
) {

    fun toImageReaction() = ImagesReactions(creationDislikeTaskMessage, rows.map {
        it.toImageReaction()
    })

}

data class ImageReactionResponseEntityRow(
    val path: String, val likes: Int, val dislikes: Int, val comment: String?
) {
    fun toImageReaction() = ImageReactionsRow(path, likes, dislikes, comment)
}
