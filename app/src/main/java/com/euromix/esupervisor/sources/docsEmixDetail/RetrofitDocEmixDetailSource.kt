package com.euromix.esupervisor.sources.docsEmixDetail

import com.euromix.esupervisor.app.model.docEmix.DocEmixDetailRepository
import com.euromix.esupervisor.app.model.docEmix.DocEmixDetailSource
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.sources.base.BaseRetrofitSource
import com.euromix.esupervisor.sources.base.RetrofitConfig
import com.euromix.esupervisor.sources.docsEmixDetail.entities.DocEmixDetailRequestAgreementEntity
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
}