package com.euromix.esupervisor.sources.docsEmixDetail

import com.euromix.esupervisor.sources.docsEmixDetail.entities.DocEmixDetailRequestAgreementEntity
import com.euromix.esupervisor.sources.docsEmixDetail.entities.DocEmixDetailResponseEntity
import retrofit2.http.*

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
}