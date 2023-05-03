package com.euromix.esupervisor.app.model.docsEmix

import com.euromix.esupervisor.app.model.*
import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import com.euromix.esupervisor.app.model.docsEmix.entities.ItemSelection
import com.euromix.esupervisor.app.utils.async.LazyFlowSubject
import com.euromix.esupervisor.sources.docsEmix.entities.DocsEmixRequestEntity
import kotlinx.coroutines.flow.Flow
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

    private val tradingAgentsFlowSubject = LazyFlowSubject<String, List<ItemSelection>> {
        docsEmixSource.findTradingAgents(it)
    }

    private val partnersFlowSubject = LazyFlowSubject<String, List<ItemSelection>> {
        docsEmixSource.findPartners(it)
    }

    fun findTradingAgents(stringSearch: String): Flow<Result<List<ItemSelection>>> {
        return tradingAgentsFlowSubject.listen(stringSearch)
    }

    fun findPartners(stringSearch: String): Flow<Result<List<ItemSelection>>> {
        return partnersFlowSubject.listen(stringSearch)
    }
}