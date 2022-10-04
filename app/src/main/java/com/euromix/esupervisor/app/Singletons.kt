package com.euromix.esupervisor.app

import android.content.Context
import android.content.res.Configuration
import com.euromix.esupervisor.App
import com.euromix.esupervisor.app.model.SourcesProvider
import com.euromix.esupervisor.app.model.accounts.AccountRepository
import com.euromix.esupervisor.app.model.accounts.AccountSource
import com.euromix.esupervisor.app.model.docEmix.DocEmixDetailRepository
import com.euromix.esupervisor.app.model.docEmix.DocEmixDetailSource
import com.euromix.esupervisor.app.model.docsEmix.DocsEmixRepository
import com.euromix.esupervisor.app.model.docsEmix.DocsEmixSource
import com.euromix.esupervisor.app.model.settings.AppSettings
import com.euromix.esupervisor.app.model.settings.SharedPreferencesAppSettings
import com.euromix.esupervisor.sources.SourceProviderHolder
import com.yariksoffice.lingver.Lingver
import java.util.*

object Singletons {

    private lateinit var appContext: Context

    private val sourcesProvider: SourcesProvider by lazy {
        SourceProviderHolder.sourcesProvider
    }

    val appSettings: AppSettings by lazy {
        SharedPreferencesAppSettings(appContext)
    }

    // --- sources

    private val accountsSource: AccountSource by lazy {
        sourcesProvider.getAccountSource()
    }

    private val docsEmixSource: DocsEmixSource by lazy {
        sourcesProvider.getDocsEmixSource()
    }

    private val docEmixDetailSource: DocEmixDetailSource by lazy {
        sourcesProvider.getDocEmixDetailSource()
    }

    // --- repositories

    val accountRepository: AccountRepository by lazy {
        AccountRepository(
            accountSource = accountsSource,
            appSettings = appSettings
        )
    }

    val docsEmixRepository: DocsEmixRepository by lazy {
        DocsEmixRepository(docsEmixSource =  docsEmixSource)
    }

    val docEmixDetailRepository: DocEmixDetailRepository by lazy {
        DocEmixDetailRepository(docEmixDetailSource = docEmixDetailSource)
    }

    fun init(appContext: Context) {
        Singletons.appContext = appContext
    }
}