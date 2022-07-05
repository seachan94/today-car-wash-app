package com.nenne.myapplication.di.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.nenne.data.api.retrofit.NetworkResponseFactory
import com.nenne.data.api.service.NaverApiService
import com.nenne.myapplication.util.Consts
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
object NaverReverseGeoCordNetworkModule {

    @Singleton
    @Provides
    @CommonNetWorkModule.ReverseGeoCord
    fun providesInterceptor() = Interceptor{
        it.proceed(
            it.request().newBuilder()
                .addHeader(Consts.NAVER_CLIENT_ID_KEY_NAME, BuildConfig.NAVER_CLIENT_ID_VALUE)
                .addHeader(Consts.NAVER_CLIENT_API_KEY_NAME, BuildConfig.NAVER_CLIENT_API_KEY_VALUE)
                .build()
        )
    }

    @Singleton
    @Provides
    @CommonNetWorkModule.ReverseGeoCord
    fun providesOkHttpClient(
        @CommonNetWorkModule.ReverseGeoCord
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
    @CommonNetWorkModule.ReverseGeoCord
    fun providesRetrofit(
        @CommonNetWorkModule.ReverseGeoCord
        okHttpClient: OkHttpClient,
        json: Json,
    ) = Retrofit.Builder().apply {
        addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        addCallAdapterFactory(NetworkResponseFactory())
        client(okHttpClient)
        baseUrl(Consts.NAVER_REVERSE_GEOCORD_BASE_URL)
    }.build()

    @Singleton
    @Provides
    fun providesNaverApiService(
        @CommonNetWorkModule.ReverseGeoCord
        retrofit : Retrofit
    ) = retrofit.create(NaverApiService::class.java)
}