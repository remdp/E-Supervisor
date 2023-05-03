package com.euromix.esupervisor.app.model.docsEmix

import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import com.euromix.esupervisor.app.model.docsEmix.entities.ItemSelection
import com.euromix.esupervisor.sources.docsEmix.entities.DocsEmixRequestEntity

interface DocsEmixSource {

    suspend fun getDocsEmix(request: DocsEmixRequestEntity?): List<DocEmix>

    suspend fun findTradingAgents(searchString: String): List<ItemSelection>

    suspend fun findPartners(searchString: String): List<ItemSelection>

}