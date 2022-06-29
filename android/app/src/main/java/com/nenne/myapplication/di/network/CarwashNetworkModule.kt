package com.nenne.myapplication.di.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.nenne.data.api.CarWashApi
import com.nenne.data.api.retrofit.NetworkResponseFactory
import com.nocompany.presentation.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CarwashNetworkModule {

    @Provides
    @Singleton
    @CommonNetWorkModule.CarWashApi
    fun providesInterceptor() = Interceptor {
        it.proceed(
            it.request().newBuilder()
                .addHeader("x-api-key",BuildConfig.CAR_WASH_HEADER_KEY)
                .build()
        )
    }

    @Singleton
    @Provides
    @CommonNetWorkModule.CarWashApi
    fun providesOkHttpClient(
        @CommonNetWorkModule.CarWashApi
        interceptor: Interceptor,
    ) = OkHttpClient.Builder().apply {
        hostnameVerifier { _, _ -> true }
        addInterceptor(interceptor)
        addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        connectTimeout(15, TimeUnit.SECONDS)
        writeTimeout(15, TimeUnit.SECONDS)
        readTimeout(15, TimeUnit.SECONDS)

    }.build()

    @Singleton
    @Provides
    @CommonNetWorkModule.CarWashApi
    fun providesRetrofit(
        @CommonNetWorkModule.CarWashApi
        okHttpClient: OkHttpClient,
        json: Json,
    ) = Retrofit.Builder().apply {
        addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        addCallAdapterFactory(NetworkResponseFactory())
        client(okHttpClient)
    }.build()

    @Singleton
    @Provides
    fun providesCarWashApi(
        @CommonNetWorkModule.CarWashApi  retrofit: Retrofit
    ) = retrofit.create(CarWashApi::class.java)
}