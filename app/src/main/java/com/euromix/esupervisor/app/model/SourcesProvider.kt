package com.euromix.esupervisor.app.model

import com.euromix.esupervisor.app.model.accounts.AccountSource
import com.euromix.esupervisor.app.model.docEmix.DocEmixDetailSource
import com.euromix.esupervisor.app.model.docsEmix.DocsEmixSource

interface SourcesProvider {

    fun getAccountSource(): AccountSource

    fun getDocsEmixSource(): DocsEmixSource

    fun getDocEmixDetailSource(): DocEmixDetailSource

}