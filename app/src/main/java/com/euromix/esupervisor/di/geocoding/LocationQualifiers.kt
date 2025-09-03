package com.euromix.esupervisor.di.geocoding

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GoogleLocationManagerQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MapboxLocationManagerQualifier