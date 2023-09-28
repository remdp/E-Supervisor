package com.euromix.esupervisor.app.model.docEmix

import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.model.docEmix.entities.ImageReaction
import com.euromix.esupervisor.app.model.docEmix.entities.ImagesReactions
import com.euromix.esupervisor.sources.docsEmixDetail.entities.ImageReactionRequestEntity

interface DocEmixDetailSource {

    suspend fun getDocEmixDetail(id: String): DocEmixDetail

    suspend fun acceptDocEmixDetail(id: String): DocEmixDetail

    suspend fun rejectDocEmixDetail(id: String, reason: String): DocEmixDetail

    suspend fun getImagesReactions(extId: String): ImagesReactions

    suspend fun react(id: String, reaction: ImageReactionRequestEntity): ImagesReactions

    suspend fun getImageReactions(id: String): List<ImageReaction>

}