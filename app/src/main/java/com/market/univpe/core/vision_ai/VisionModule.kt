package com.market.univpe.core.vision_ai

import com.market.univpe.core.vision_ai.remote.VisionCloudRunApiService
import com.market.univpe.feature.home.domain.repository.VisionAiRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VisionModule {

    @Binds
    @Singleton
    abstract fun bindVisionAiRepository(
        visionAiRepositoryImpl: VisionAiRepositoryImpl
    ): VisionAiRepository

    companion object {
        private const val VISION_CLOUD_RUN_BASE_URL = "https://market-ai-api-367008890014.southamerica-west1.run.app/"

        @Provides
        @Singleton
        fun provideVisionRetrofit(
            okHttpClient: OkHttpClient,
            gsonFactory: GsonConverterFactory
        ): Retrofit {
            return Retrofit.Builder()
                .baseUrl(VISION_CLOUD_RUN_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(gsonFactory)
                .build()
        }


        @Provides
        @Singleton
        fun provideVisionCloudRunApiService(retrofit: Retrofit): VisionCloudRunApiService {
            return retrofit.create(VisionCloudRunApiService::class.java)
        }
    }
}