package com.example.marketuniversitario.core.vision_ai

import android.net.Uri
import com.example.marketuniversitario.core.network.util.NetworkResult
import com.example.marketuniversitario.core.vision_ai.remote.CloudRunVisionService
import com.example.marketuniversitario.feature.home.domain.repository.VisionAiRepository
import javax.inject.Inject

class VisionAiRepositoryImpl @Inject constructor(
    private val cloudRunService: CloudRunVisionService
) : VisionAiRepository {

    override suspend fun extractTagsFromImage(imageUri: Uri): NetworkResult<List<String>> {
        return cloudRunService.extractTagsFromImage(imageUri)
    }
}