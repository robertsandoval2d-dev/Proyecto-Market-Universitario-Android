package com.example.marketuniversitario.feature.home.ui.components.SearchOptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.home.domain.usecases.GetFeedProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchWithImageViewModel @Inject constructor(
    private val getFeedProductsUseCase: GetFeedProductsUseCase
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
        if (_state.value.selectedImageUri == null) return
        viewModelScope.launch {
            _state.update { it.copy(status = SearchWithImageStatus.Analyzing) }
            try {
                // 1. Obtener todos los productos del feed
                val allProducts = getFeedProductsUseCase().getOrDefault(emptyList())

                // TODO: Reemplazar esto con tu llamada real a la API de IA que lee el .md y retorna etiquetas (ej: ["Calculadora", "Casio"])
                // Simulación de etiquetas detectadas basadas en la foto:
                val mockDetectedTags = listOf("Calculadora", "Electrónica", "Estudio", "Casio")

                // 2. Filtrar productos locales que coincidan con las etiquetas detectadas o el nombre
                val matched = allProducts.filter { product ->
                    product.allTags.any { tag -> mockDetectedTags.any { it.equals(tag, ignoreCase = true) } } ||
                            mockDetectedTags.any { query -> product.name.contains(query, ignoreCase = true) }
                }

                _state.update {
                    it.copy(
                        status = SearchWithImageStatus.Success(
                            detectedTags = mockDetectedTags,
                            matchedProducts = matched
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
