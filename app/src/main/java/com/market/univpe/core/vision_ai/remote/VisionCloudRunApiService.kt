package com.market.univpe.core.vision_ai.remote

import com.market.univpe.core.vision_ai.dto.PredictionResponseDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface VisionCloudRunApiService {

    @Multipart
    @POST("extract-tags")
    suspend fun predictImage(
        @Part image: MultipartBody.Part
    ): Response<PredictionResponseDto>
}