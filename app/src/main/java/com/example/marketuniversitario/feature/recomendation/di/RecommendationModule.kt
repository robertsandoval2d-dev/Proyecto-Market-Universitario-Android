package com.example.marketuniversitario.feature.recomendation.di

import com.example.marketuniversitario.feature.recomendation.data.repositories.AiRecommendationRepositoryImpl
import com.example.marketuniversitario.feature.recomendation.data.repositories.InteractionRepositoryImpl
import com.example.marketuniversitario.feature.recomendation.domain.repositories.AiRecommendationRepository
import com.example.marketuniversitario.feature.recomendation.domain.repositories.InteractionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RecommendationModule {

    @Binds
    @Singleton
    abstract fun bindInteractionRepository(
        impl: InteractionRepositoryImpl
    ): InteractionRepository

    @Binds
    @Singleton
    abstract fun bindAiRecommendationRepository(
        impl: AiRecommendationRepositoryImpl
    ): AiRecommendationRepository
}
