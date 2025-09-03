package com.euromix.esupervisor.di

import com.euromix.esupervisor.app.usecases.FetchFiltersDataUseCase
import com.euromix.esupervisor.app.usecases.FetchFiltersForCreateTasksUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {
    @Binds
    abstract fun bindFetchFiltersForCreateTasksUseCase(
        impl: FetchFiltersForCreateTasksUseCaseImpl
    ): FetchFiltersDataUseCase
}