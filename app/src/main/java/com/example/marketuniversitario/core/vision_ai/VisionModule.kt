package com.example.marketuniversitario.core.vision_ai

import com.example.marketuniversitario.feature.home.domain.repository.VisionAiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VisionModule {

    @Binds
    @Singleton
    abstract fun bindVisionAiRepository(
        visionAiRepositoryImpl: VisionAiRepositoryImpl
    ): VisionAiRepository
}
