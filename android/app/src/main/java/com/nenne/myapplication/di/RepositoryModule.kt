package com.nenne.myapplication.di

import com.nenne.data.repository.CarWashShopLocationRepositoryImpl
import com.nenne.data.repository.NaverMapReverseGeoCodeRepositoryImpl
import com.nenne.domain.repository.carwash.CarWashShopLocationRepository
import com.nenne.domain.repository.naverapi.NaverMapReverseGeoCodeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsCarWashShopLocationRepository(
        carWashShopLocationRepositoryImpl: CarWashShopLocationRepositoryImpl): CarWashShopLocationRepository

    @Binds
    @Singleton
    abstract fun bindsNaverMapReverseGeoCodeRepository(
        naverMapReverseGeoCodeRepositoryImpl: NaverMapReverseGeoCodeRepositoryImpl
    ): NaverMapReverseGeoCodeRepository
}