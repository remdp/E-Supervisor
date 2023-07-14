package com.euromix.esupervisor.sources.docsEmix

import com.euromix.esupervisor.app.model.docsEmix.DocsEmixSource
import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import com.euromix.esupervisor.sources.base.BaseRetrofitSource
import com.euromix.esupervisor.sources.base.RetrofitConfig
import com.euromix.esupervisor.sources.docsEmix.entities.DocsEmixRequestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitDocsEmixSource @Inject constructor(
    private val config: RetrofitConfig
) : BaseRetrofitSource(config), DocsEmixSource {

    private val docsEmixApi = retrofit.create(DocsEmixApi::class.java)

    override suspend fun getDocsEmix(request: DocsEmixRequestEntity?): List<DocEmix> {
        return wrapRetrofitException {

            val jsonAdapter = config.moshi.adapter(DocsEmixRequestEntity::class.java)

            val response = docsEmixApi.getDocsEmix(jsonAdapter.toJson(request))
            response.map {
                it.toDocEmix()
            }
        }
    }

//    override suspend fun findTradingAgents(searchString: String): List<ServerPair> {
//        return wrapRetrofitException {
//            docsEmixApi.findTradingAgents(searchString)
//        }
//    }
//
//    override suspend fun findPartners(searchString: String): List<ServerPair> {
//        return wrapRetrofitException {
//            docsEmixApi.findPartners(searchString)
//        }
//    }
}