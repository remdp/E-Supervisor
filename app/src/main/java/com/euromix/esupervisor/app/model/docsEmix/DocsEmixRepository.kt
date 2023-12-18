package com.euromix.esupervisor.app.model.docsEmix

import com.euromix.esupervisor.app.utils.async.serverCallbackFlowFetcher
import com.euromix.esupervisor.sources.docsEmix.entities.DocsEmixRequestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocsEmixRepository @Inject constructor(
    private val docsEmixSource: DocsEmixSource
) {
    fun getDocsEmix(request: DocsEmixRequestEntity) =
        serverCallbackFlowFetcher { docsEmixSource.getDocsEmix(request) }
}