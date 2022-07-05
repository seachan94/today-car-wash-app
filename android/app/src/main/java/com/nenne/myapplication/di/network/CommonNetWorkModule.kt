package com.nenne.myapplication.di.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonNetWorkModule {

    @Provides
    @Singleton
    fun providerConverterFactory() : Json =
        Json{
            encodeDefaults =true
            coerceInputValues = true
            ignoreUnknownKeys = true
            isLenient = true
        }

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CarWashApi

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ReverseGeoCord
}