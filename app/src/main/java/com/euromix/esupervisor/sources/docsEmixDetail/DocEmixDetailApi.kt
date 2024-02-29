package com.euromix.esupervisor.sources.docsEmixDetail

import com.euromix.esupervisor.sources.docsEmixDetail.entities.ChangeCoordinatesResponseEntity
import com.euromix.esupervisor.sources.docsEmixDetail.entities.DocEmixDetailRequestAgreementEntity
import com.euromix.esupervisor.sources.docsEmixDetail.entities.DocEmixDetailResponseEntity
import com.euromix.esupervisor.sources.docsEmixDetail.entities.ImageReactionRequestEntity
import com.euromix.esupervisor.sources.docsEmixDetail.entities.ImageReactionResponseEntity
import com.euromix.esupervisor.sources.docsEmixDetail.entities.ImagesReactionsResponseEntity
import retrofit2.http.*

//todo separate functions for document and reactions
interface DocEmixDetailApi {

    @GET("unidoc")
    suspend fun getDocEmixDetail(@Header("id") id: String): DocEmixDetailResponseEntity

    @POST("unidoc")
    suspend fun acceptDocEmixDetail(
        @Header("id") id: String,
        @Body bodyAgreement: DocEmixDetailRequestAgreementEntity
    ): DocEmixDetailResponseEntity

    @POST("unidoc")
    suspend fun rejectDocEmixDetail(
        @Header("id") id: String,
        @Body bodyAgreement: DocEmixDetailRequestAgreementEntity
    ): DocEmixDetailResponseEntity

    @GET("images_reactions")
    suspend fun getImagesReactions(@Header("id") photoIds: String): ImagesReactionsResponseEntity

    @POST("react")
    suspend fun react(
        @Header("id") id: String,
        @Body bodyReact: ImageReactionRequestEntity
    ): ImagesReactionsResponseEntity

    @GET("image_reactions")
    suspend fun getImageReactions(@Header("id") id: String): List<ImageReactionResponseEntity>

    @GET("change_coordinates")
    suspend fun getChangeCoordinates(@Header("id") id: String): ChangeCoordinatesResponseEntity

    @POST("change_coordinates")
    suspend fun acceptChangeCoordinates(
        @Header("id") id: String,
        @Body bodyAgreement: DocEmixDetailRequestAgreementEntity
    )
}