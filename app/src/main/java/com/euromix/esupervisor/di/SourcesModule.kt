package com.euromix.esupervisor.di

import com.euromix.esupervisor.app.model.accounts.AccountSource
import com.euromix.esupervisor.app.model.docEmix.DocEmixDetailSource
import com.euromix.esupervisor.app.model.docsEmix.DocsEmixSource
import com.euromix.esupervisor.sources.account.RetrofitAccountSource
import com.euromix.esupervisor.sources.docsEmix.RetrofitDocsEmixSource
import com.euromix.esupervisor.sources.docsEmixDetail.RetrofitDocEmixDetailSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SourcesModule {

    @Binds
    abstract fun bindAccountSource(retrofitAccountSource: RetrofitAccountSource ):AccountSource

    @Binds
    abstract fun bindDocEmixDetailSource(retrofitDocEmixDetailSource: RetrofitDocEmixDetailSource):DocEmixDetailSource

    @Binds
    abstract fun bindDocEmixSource(retrofitDocsEmixSource: RetrofitDocsEmixSource):DocsEmixSource
}