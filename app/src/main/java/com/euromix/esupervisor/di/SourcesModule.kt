package com.euromix.esupervisor.di

import com.euromix.esupervisor.app.model.account.AccountSource
import com.euromix.esupervisor.app.model.common.SearchSource
import com.euromix.esupervisor.app.model.docEmix.DocEmixDetailSource
import com.euromix.esupervisor.app.model.docsEmix.DocsEmixSource
import com.euromix.esupervisor.app.model.rates.RatesSource
import com.euromix.esupervisor.app.model.taskDetail.TaskDetailSource
import com.euromix.esupervisor.app.model.tasks.TasksSource
import com.euromix.esupervisor.sources.account.RetrofitAccountSource
import com.euromix.esupervisor.sources.common.RetrofitSearchSource
import com.euromix.esupervisor.sources.docsEmix.RetrofitDocsEmixSource
import com.euromix.esupervisor.sources.docsEmixDetail.RetrofitDocEmixDetailSource
import com.euromix.esupervisor.sources.salesRate.RetrofitRatesSource
import com.euromix.esupervisor.sources.tasks.taskDetail.RetrofitTaskDetailSource
import com.euromix.esupervisor.sources.tasks.list.RetrofitTasksSource
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
    abstract fun bindDocsEmixSource(retrofitDocsEmixSource: RetrofitDocsEmixSource):DocsEmixSource

    @Binds
    abstract fun bindRatesSource(retrofitRatesSource: RetrofitRatesSource): RatesSource

    @Binds
    abstract fun bindTasksSource(retrofitTasksSource: RetrofitTasksSource): TasksSource

    @Binds
    abstract fun bindTaskDetailSource(retrofitTaskDetailSource: RetrofitTaskDetailSource): TaskDetailSource

    @Binds
    abstract fun bindSearchSource(retrofitSearchSource: RetrofitSearchSource): SearchSource

}