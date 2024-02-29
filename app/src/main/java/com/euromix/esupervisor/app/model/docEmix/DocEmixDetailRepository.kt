package com.euromix.esupervisor.app.model.docEmix

import com.euromix.esupervisor.app.utils.async.serverCallbackFlowFetcher
import com.euromix.esupervisor.sources.docsEmixDetail.entities.ImageReactionRequestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocEmixDetailRepository @Inject constructor(
    private val docEmixDetailSource: DocEmixDetailSource
) {

    fun getDocEmixDetail(extId: String) =
        serverCallbackFlowFetcher { docEmixDetailSource.getDocEmixDetail(extId) }

    fun acceptDocEmixDetail(extId: String) =
        serverCallbackFlowFetcher { docEmixDetailSource.acceptDocEmixDetail(extId) }

    fun rejectDocEmixDetail(extId: String, reason: String) =
        serverCallbackFlowFetcher { docEmixDetailSource.rejectDocEmixDetail(extId, reason) }

    fun getDocLikes(id: String) =
        serverCallbackFlowFetcher { docEmixDetailSource.getImagesReactions(id) }

    fun react(id: String, reaction: ImageReactionRequestEntity) =
        serverCallbackFlowFetcher { docEmixDetailSource.react(id, reaction) }

    fun getImageReactions(id: String) =
        serverCallbackFlowFetcher { docEmixDetailSource.getImageReactions(id) }

    fun getChangeCoordinates(id: String) =
        serverCallbackFlowFetcher { docEmixDetailSource.getChangeCoordinates(id) }

    fun acceptChangeCoordinates(id: String, approve: Boolean, reason: String) =
        serverCallbackFlowFetcher {
            docEmixDetailSource.acceptChangeCoordinates(
                id,
                approve,
                reason
            )
        }
}