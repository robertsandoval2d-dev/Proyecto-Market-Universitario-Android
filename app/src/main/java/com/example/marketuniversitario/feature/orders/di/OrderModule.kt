package com.example.marketuniversitario.feature.orders.di

import com.example.marketuniversitario.feature.orders.data.repositories.OrderRepositoryImpl
import com.example.marketuniversitario.feature.orders.domain.repositories.OrderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OrderModule {

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        impl: OrderRepositoryImpl
    ): OrderRepository

}
