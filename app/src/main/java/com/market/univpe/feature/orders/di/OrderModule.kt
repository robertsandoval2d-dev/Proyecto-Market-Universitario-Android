package com.market.univpe.feature.orders.di

import com.market.univpe.feature.orders.data.datasources.AndroidAudioPlayer
import com.market.univpe.feature.orders.data.datasources.AndroidAudioRecorder
import com.market.univpe.feature.orders.data.repositories.OrderRepositoryImpl
import com.market.univpe.feature.orders.domain.repositories.AudioPlayer
import com.market.univpe.feature.orders.domain.repositories.AudioRecorder
import com.market.univpe.feature.orders.domain.repositories.OrderRepository
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

    @Binds
    @Singleton
    abstract fun bindAudioRecorder(
        impl: AndroidAudioRecorder
    ): AudioRecorder

    @Binds
    @Singleton
    abstract fun bindAudioPlayer(
        impl: AndroidAudioPlayer
    ): AudioPlayer
}
