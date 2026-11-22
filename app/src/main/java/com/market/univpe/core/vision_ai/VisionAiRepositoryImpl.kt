package com.market.univpe.core.vision_ai

import android.net.Uri
import com.market.univpe.core.network.util.NetworkResult
import com.market.univpe.core.vision_ai.remote.CloudRunVisionService
import com.market.univpe.feature.home.domain.repository.VisionAiRepository
import javax.inject.Inject

class VisionAiRepositoryImpl @Inject constructor(
    private val cloudRunService: CloudRunVisionService
) : VisionAiRepository {

    override suspend fun extractTagsFromImage(imageUri: Uri): NetworkResult<List<String>> {
        return cloudRunService.extractTagsFromImage(imageUri)
    }
}