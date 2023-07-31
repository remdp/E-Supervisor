package com.euromix.esupervisor.app.model.docsEmix

import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import com.euromix.esupervisor.app.utils.async.LazyFlowSubject
import com.euromix.esupervisor.app.utils.async.ValueListener
import com.euromix.esupervisor.sources.docsEmix.entities.DocsEmixRequestEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocsEmixRepository @Inject constructor(
    private val docsEmixSource: DocsEmixSource
) {

    private val docsEmixLazyFlowSubject = LazyFlowSubject<DocsEmixRequestEntity, List<DocEmix>> {
        docsEmixSource.getDocsEmix(it)
    }

    fun getDocsEmix(request: DocsEmixRequestEntity): Flow<Result<List<DocEmix>>> {
        return docsEmixLazyFlowSubject.listen(request)
    }

    fun getDocsEmixNew(request: DocsEmixRequestEntity): Flow<Result<List<DocEmix>>> {

        return callbackFlow {
            trySend(Success(docsEmixSource.getDocsEmix(request)))
            awaitClose {  }

        }


    }

//    private val tradingAgentsFlowSubject = LazyFlowSubject<String, List<ServerPair>> {
//        docsEmixSource.findTradingAgents(it)
//    }
//
//    private val partnersFlowSubject = LazyFlowSubject<String, List<ServerPair>> {
//        docsEmixSource.findPartners(it)
//    }
//
//    fun findTradingAgents(stringSearch: String): Flow<Result<List<ServerPair>>> {
//        return tradingAgentsFlowSubject.listen(stringSearch)
//    }
//
//    fun findPartners(stringSearch: String): Flow<Result<List<ServerPair>>> {
//        return partnersFlowSubject.listen(stringSearch)
//    }
}