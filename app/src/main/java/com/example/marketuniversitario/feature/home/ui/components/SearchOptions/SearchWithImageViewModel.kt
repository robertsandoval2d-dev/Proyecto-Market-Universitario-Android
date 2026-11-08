package com.example.marketuniversitario.feature.home.ui.components.SearchOptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.home.domain.usecases.GetFeedProductsUseCase
import com.example.marketuniversitario.feature.home.domain.usecases.GetTagsFromImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchWithImageViewModel @Inject constructor(
    private val getFeedProductsUseCase: GetFeedProductsUseCase,
    private val getTagsFromImageUseCase: GetTagsFromImageUseCase
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
                val tags = getTagsFromImageUseCase(uri.toString())

                _state.update {
                        it.copy(
                            status = SearchWithImageStatus.Success(
                                detectedTags = tags
                            )
                        )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(status = SearchWithImageStatus.Error(e.message ?: "Error al analizar la imagen"))
                }
            }
        }
    }
}
