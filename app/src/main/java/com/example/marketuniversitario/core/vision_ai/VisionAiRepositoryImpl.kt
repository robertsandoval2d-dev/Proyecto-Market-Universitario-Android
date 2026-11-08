package com.example.marketuniversitario.core.vision_ai

import com.example.marketuniversitario.core.vision_ai.onnx.OnnxVisionService
import com.example.marketuniversitario.feature.home.domain.repository.VisionAiRepository
import javax.inject.Inject

class VisionAiRepositoryImpl @Inject constructor(
    private val onnxService: OnnxVisionService
) : VisionAiRepository {

    override suspend fun extractTagsFromImage(imageUriString: String): List<String> =
        onnxService.extractTagsFromImage(imageUriString)
}
