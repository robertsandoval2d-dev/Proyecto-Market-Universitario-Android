package com.example.marketuniversitario.core.vision_ai

interface VisionAiService {
    suspend fun extractTagsFromImage(
        imageUriString: String
    ): List<String>
}
