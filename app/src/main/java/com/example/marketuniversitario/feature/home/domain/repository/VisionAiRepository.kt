package com.example.marketuniversitario.feature.home.domain.repository

interface VisionAiRepository {
    suspend fun extractTagsFromImage(imageUriString: String): List<String>
}