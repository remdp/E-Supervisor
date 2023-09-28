package com.euromix.esupervisor.sources.docsEmixDetail.entities

import com.euromix.esupervisor.app.model.docEmix.entities.ImageReaction

data class ImageReactionResponseEntity(
    val user: String,
    val reaction: Int,
    val comment: String
) {
    fun toImageReaction(id: Long) = ImageReaction(id, user, reaction, comment)
}