package com.euromix.esupervisor.sources.base

import com.euromix.esupervisor.app.model.SourcesProvider
import com.euromix.esupervisor.app.model.accounts.AccountSource
import com.euromix.esupervisor.app.model.docEmix.DocEmixDetailSource
import com.euromix.esupervisor.app.model.docsEmix.DocsEmixSource
import com.euromix.esupervisor.sources.account.RetrofitAccountSource
import com.euromix.esupervisor.sources.docsEmix.RetrofitDocsEmixSource
import com.euromix.esupervisor.sources.docsEmixDetail.RetrofitDocEmixDetailSource

class RetrofitSourcesProvider(
    private val config: RetrofitConfig
) :SourcesProvider{

    override fun getAccountSource(): AccountSource {
        return RetrofitAccountSource(config)
    }

    override fun getDocsEmixSource(): DocsEmixSource {
        return RetrofitDocsEmixSource(config)
    }

    override fun getDocEmixDetailSource(): DocEmixDetailSource {
        return RetrofitDocEmixDetailSource(config)
    }
}