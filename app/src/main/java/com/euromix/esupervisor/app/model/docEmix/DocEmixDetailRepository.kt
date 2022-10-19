package com.euromix.esupervisor.app.model.docEmix

import com.euromix.esupervisor.app.model.AuthException
import com.euromix.esupervisor.app.model.BackendException
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.model.wrapBackendExceptions
import com.euromix.esupervisor.app.utils.async.LazyFlowSubject
import com.euromix.esupervisor.sources.docsEmixDetail.entities.DocEmixDetailRequestAgreementEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocEmixDetailRepository @Inject constructor(
    private val docEmixDetailSource: DocEmixDetailSource
) {

    private val docEmixDetailLazyFlowSubject = LazyFlowSubject<Map<String, Any>, DocEmixDetail> {
        wrapBackendExceptions {
            try {
                if (it.containsKey(APPROVE)) {

                    if (it[APPROVE] as Boolean)
                        return@LazyFlowSubject docEmixDetailSource.acceptDocEmixDetail(it[EXTID].toString())
                    else
                        return@LazyFlowSubject docEmixDetailSource.rejectDocEmixDetail(
                            it[EXTID].toString(),
                            it[BODY_AGREEMENT].toString()
                        )
                } else {
                    return@LazyFlowSubject docEmixDetailSource.getDocEmixDetail(it[EXTID].toString())
                }

            } catch (e: BackendException) {
                if (e.code == 404) throw AuthException(e)
                else throw e
            }
        }
    }

    fun getDocEmixDetail(extId: String): Flow<Result<DocEmixDetail>> {
        return docEmixDetailLazyFlowSubject.listen(mapOf(EXTID to extId))
    }

    fun reload(extId: String){
        docEmixDetailLazyFlowSubject.reloadArgument(mapOf(EXTID to extId))
    }

    fun acceptDocEmixDetail(extId: String): Flow<Result<DocEmixDetail>> {
        return docEmixDetailLazyFlowSubject.listen(mapOf(EXTID to extId, APPROVE to true))
    }

    fun rejectDocEmixDetail(extId: String, reason: String): Flow<Result<DocEmixDetail>> {

        return docEmixDetailLazyFlowSubject.listen(
            mapOf(
                EXTID to extId,
                APPROVE to false,
                BODY_AGREEMENT to reason
            )
        )
    }


    companion object {
        const val EXTID = "id"
        const val APPROVE = "approve"
        const val BODY_AGREEMENT = "bodyAgreement"

    }

}