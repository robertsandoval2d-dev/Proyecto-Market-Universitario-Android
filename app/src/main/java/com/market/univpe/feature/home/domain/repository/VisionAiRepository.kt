package com.market.univpe.feature.home.domain.repository

import android.net.Uri
import com.market.univpe.core.network.util.NetworkResult

interface VisionAiRepository {
    suspend fun extractTagsFromImage(imageUri: Uri): NetworkResult<List<String>>
}