package com.market.univpe.feature.reviews.di

import com.market.univpe.feature.reviews.data.repositories.ReviewRepositoryImpl
import com.market.univpe.feature.reviews.domain.repositories.ReviewRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReviewModule {

    @Binds
    @Singleton
    abstract fun bindReviewRepository(
        impl: ReviewRepositoryImpl
    ): ReviewRepository
}
