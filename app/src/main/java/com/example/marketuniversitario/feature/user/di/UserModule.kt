package com.example.marketuniversitario.feature.user.di

import com.example.marketuniversitario.feature.user.data.repositories.UserRepositoryImpl
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
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
}