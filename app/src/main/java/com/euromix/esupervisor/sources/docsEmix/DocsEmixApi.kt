package com.euromix.esupervisor.sources.docsEmix

import com.euromix.esupervisor.sources.docsEmix.entities.DocsEmixResponseEntity
import retrofit2.http.GET
import retrofit2.http.Header

interface DocsEmixApi {

    @GET("unidocs")
    suspend fun getDocsEmix(
        @Header("request") request: String? = null
    ): List<DocsEmixResponseEntity>

//    @GET("trading_agents")
//    suspend fun findTradingAgents(@Query("search_string") searchString: String): List<ServerPair>
//
//    @GET("partners")
//    suspend fun findPartners(@Query("search_string") searchString: String): List<ServerPair>

}