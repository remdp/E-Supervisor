package com.euromix.esupervisor.app.model.docsEmix

import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import kotlinx.coroutines.flow.Flow

interface DocsEmixSource {

    suspend fun getDocsEmix(): List<DocEmix>

}