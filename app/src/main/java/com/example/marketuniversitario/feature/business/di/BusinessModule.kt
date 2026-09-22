package com.example.marketuniversitario.feature.business.di

import com.example.marketuniversitario.feature.business.data.repositories.BusinessRepositoryImpl
import com.example.marketuniversitario.feature.business.domain.repositories.BusinessRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BusinessModule {

    @Binds
    @Singleton
    abstract fun bindBusinessRepository(
        impl: BusinessRepositoryImpl
    ): BusinessRepository
}
