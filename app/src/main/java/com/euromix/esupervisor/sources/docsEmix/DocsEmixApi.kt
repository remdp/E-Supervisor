package com.euromix.esupervisor.sources.docsEmix

import com.euromix.esupervisor.sources.docsEmix.entities.DocsEmixResponseEntity
import retrofit2.http.GET


interface DocsEmixApi {

    @GET("unidocs")
    suspend fun getDocsEmix(): List<DocsEmixResponseEntity>

}