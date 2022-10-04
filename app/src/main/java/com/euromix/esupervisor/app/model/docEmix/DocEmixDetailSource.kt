package com.euromix.esupervisor.app.model.docEmix

import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.sources.docsEmixDetail.entities.DocEmixDetailRequestAgreementEntity

interface DocEmixDetailSource {

    suspend fun getDocEmixDetail(id: String): DocEmixDetail

    suspend fun acceptDocEmixDetail(id: String): DocEmixDetail

    suspend fun rejectDocEmixDetail(id: String, reason: String): DocEmixDetail

}