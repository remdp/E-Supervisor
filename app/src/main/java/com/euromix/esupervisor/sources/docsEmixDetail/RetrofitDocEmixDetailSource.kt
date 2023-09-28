package com.euromix.esupervisor.sources.docsEmixDetail

import com.euromix.esupervisor.app.model.docEmix.DocEmixDetailSource
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.model.docEmix.entities.ImageReaction
import com.euromix.esupervisor.app.model.docEmix.entities.ImagesReactions
import com.euromix.esupervisor.sources.base.BaseRetrofitSource
import com.euromix.esupervisor.sources.base.RetrofitConfig
import com.euromix.esupervisor.sources.docsEmixDetail.entities.DocEmixDetailRequestAgreementEntity
import com.euromix.esupervisor.sources.docsEmixDetail.entities.ImageReactionRequestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitDocEmixDetailSource @Inject constructor(config: RetrofitConfig) :
    BaseRetrofitSource(config),
    DocEmixDetailSource {

    private val docEmixDetailApi = retrofit.create(DocEmixDetailApi::class.java)

    override suspend fun getDocEmixDetail(id: String): DocEmixDetail {

        return wrapRetrofitException {
            val response = docEmixDetailApi.getDocEmixDetail(id)
            response.toDocEmixDetail()
        }
    }

    override suspend fun acceptDocEmixDetail(id: String): DocEmixDetail {

        return wrapRetrofitException {
            val response = docEmixDetailApi.acceptDocEmixDetail(
                id,
                DocEmixDetailRequestAgreementEntity(true, null)
            )
            response.toDocEmixDetail()
        }
    }

    override suspend fun rejectDocEmixDetail(id: String, reason: String): DocEmixDetail {
        return wrapRetrofitException {
            val response = docEmixDetailApi.rejectDocEmixDetail(
                id,
                DocEmixDetailRequestAgreementEntity(false, reason)
            )
            response.toDocEmixDetail()
        }
    }

    override suspend fun getImagesReactions(extId: String): ImagesReactions {
        return wrapRetrofitException {
            docEmixDetailApi.getImagesReactions(extId).toImageReaction()
        }
    }

    override suspend fun react(
        id: String,
        reaction: ImageReactionRequestEntity
    ): ImagesReactions {
        return wrapRetrofitException {
            docEmixDetailApi.react(id, reaction).toImageReaction()
        }
    }

    override suspend fun getImageReactions(id: String): List<ImageReaction> {
        return wrapRetrofitException {
            var counter = -1L
            docEmixDetailApi.getImageReactions(id).map {
                counter++
                it.toImageReaction(counter)
            }
        }
    }
}