package com.example.marketuniversitario.feature.home.ui.components.SearchOptions

import android.net.Uri

sealed interface SearchWithImageStatus {
    object Idle : SearchWithImageStatus
    object Analyzing : SearchWithImageStatus
    data class Success(val detectedTags: List<String>) : SearchWithImageStatus
    data class Error(val message: String) : SearchWithImageStatus
}

data class SearchWithImageState(
    val selectedImageUri: Uri? = null,

    val status: SearchWithImageStatus = SearchWithImageStatus.Idle
)

sealed interface SearchWithImageEvent {
    data class ImageSelected(val uri: Uri?) : SearchWithImageEvent
    object AnalyzeImage : SearchWithImageEvent
    object ClearImage : SearchWithImageEvent
    object Dismiss : SearchWithImageEvent
}
