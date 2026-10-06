package com.example.marketuniversitario.feature.home.ui.components.SearchOptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchWithImageViewModel @Inject constructor(
    // TODO: Inyectar aquí el UseCase o Cliente API para análisis de imágenes IA
) : ViewModel() {

    private val _state = MutableStateFlow(SearchWithImageState())
    val state: StateFlow<SearchWithImageState> = _state.asStateFlow()

    fun onEvent(event: SearchWithImageEvent) {
        when (event) {
            is SearchWithImageEvent.ImageSelected -> {
                _state.update {
                    it.copy(
                        selectedImageUri = event.uri,
                        status = SearchWithImageStatus.Idle
                    )
                }
            }
            is SearchWithImageEvent.AnalyzeImage -> analyzeImage()
            is SearchWithImageEvent.ClearImage -> {
                _state.update {
                    it.copy(
                        selectedImageUri = null,
                        status = SearchWithImageStatus.Idle
                    )
                }
            }
            is SearchWithImageEvent.Dismiss -> {
                _state.update { SearchWithImageState() }
            }
        }
    }

    private fun analyzeImage() {
        val uri = _state.value.selectedImageUri ?: return
        viewModelScope.launch {
            _state.update { it.copy(status = SearchWithImageStatus.Analyzing) }
            try {
                // TODO: Conectar con la API de IA (ej: Gemini Vision / Backend)
                // val detectedTags = aiVisionRepository.analyzePhotoWithPrompt(uri)
                // _state.update { it.copy(status = SearchWithImageStatus.Success(detectedTags))
            } catch (e: Exception) {
                _state.update {
                    it.copy(status = SearchWithImageStatus.Error(e.message ?: "Error al identificar etiquetas"))
                }
            }
        }
    }
}
