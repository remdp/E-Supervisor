package com.euromix.esupervisor.sources.docsEmix

import com.euromix.esupervisor.app.model.docsEmix.DocsEmixSource
import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import com.euromix.esupervisor.sources.base.BaseRetrofitSource
import com.euromix.esupervisor.sources.base.RetrofitConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitDocsEmixSource @Inject constructor(
    config: RetrofitConfig
) : BaseRetrofitSource(config), DocsEmixSource {

    private val docsEmixApi = retrofit.create(DocsEmixApi::class.java)

    override suspend fun getDocsEmix(): List<DocEmix> {
        return wrapRetrofitException {
            val response = docsEmixApi.getDocsEmix()
            response.map {
                it.toDocEmix()
            }
        }
    }
}