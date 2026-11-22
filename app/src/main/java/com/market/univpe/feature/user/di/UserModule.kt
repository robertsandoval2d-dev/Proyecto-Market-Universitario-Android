package com.market.univpe.feature.user.di

import com.market.univpe.feature.user.data.repositories.StorageRepositoryImpl
import com.market.univpe.feature.user.data.repositories.UserRepositoryImpl
import com.market.univpe.feature.user.domain.repositories.StorageRepository
import com.market.univpe.feature.user.domain.repositories.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindStorageRepository(
        impl: StorageRepositoryImpl
    ): StorageRepository
}