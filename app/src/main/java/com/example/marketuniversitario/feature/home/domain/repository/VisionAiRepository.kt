package com.example.marketuniversitario.feature.home.domain.repository

import android.net.Uri
import com.example.marketuniversitario.core.network.util.NetworkResult

interface VisionAiRepository {
    suspend fun extractTagsFromImage(imageUri: Uri): NetworkResult<List<String>>
}