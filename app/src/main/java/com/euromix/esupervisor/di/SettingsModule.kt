package com.euromix.esupervisor.di

import com.euromix.esupervisor.app.model.settings.AppSettings
import com.euromix.esupervisor.app.model.settings.SharedPreferencesAppSettings
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    abstract fun bindAppSettings(appSettings: SharedPreferencesAppSettings): AppSettings

}