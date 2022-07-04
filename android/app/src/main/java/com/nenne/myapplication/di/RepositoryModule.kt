package com.nenne.myapplication.di

import com.nenne.data.repository.CarWashShopLocationRepositoryImpl
import com.nenne.domain.repository.CarWashShopLocationRepository
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
        carWashShopLocationRepositoryImpl: CarWashShopLocationRepositoryImpl):CarWashShopLocationRepository
}