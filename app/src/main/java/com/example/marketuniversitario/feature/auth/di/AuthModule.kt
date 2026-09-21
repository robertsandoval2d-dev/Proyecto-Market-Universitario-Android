package com.example.marketuniversitario.feature.auth.di

import com.example.marketuniversitario.feature.auth.data.repositories.AuthRepositoryImpl
import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

}