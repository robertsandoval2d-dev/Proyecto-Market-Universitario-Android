package com.example.marketuniversitario.feature.business.ui.product_detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.auth.domain.usecases.GetCurrentUserIdUseCase
import com.example.marketuniversitario.feature.business.domain.usecases.GetPublicProductUseCase
import com.example.marketuniversitario.feature.business.domain.usecases.IncrementProductViewUseCase
import com.example.marketuniversitario.feature.recomendation.domain.models.InteractionType
import com.example.marketuniversitario.feature.recomendation.domain.usecases.TrackInteractionUseCase
import com.example.marketuniversitario.feature.reviews.domain.usecases.GetReviewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getPublicProductUseCase: GetPublicProductUseCase,
    private val getReviewsUseCase: GetReviewsUseCase,
    private val incrementProductViewUseCase: IncrementProductViewUseCase,
    private val trackInteractionUseCase: TrackInteractionUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailState())
    val state: StateFlow<ProductDetailState> = _state.asStateFlow()

    init {
        val productId = savedStateHandle.get<String>("productId")
        if (!productId.isNullOrBlank()) {
            loadProduct(productId)
        }
    }

    fun onEvent(event: ProductDetailEvent) {
        when (event) {
            is ProductDetailEvent.LoadProduct -> loadProduct(event.productId)
            is ProductDetailEvent.NewCommentChanged -> _state.update { it.copy(newCommentText = event.text) }
            is ProductDetailEvent.NewRatingChanged -> _state.update { it.copy(newCommentRating = event.rating) }
            is ProductDetailEvent.AddComment -> addComment()
            is ProductDetailEvent.ToggleFavorite -> _state.update { it.copy(isFavorite = !it.isFavorite) }
        }
    }

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _state.update { it.copy(status = ProductDetailStatus.Loading) }

            // 1. Incrementar vistas y trackear interacción VIEW en segundo plano con trazabilidad
            launch {
                incrementProductViewUseCase(productId)
                    .onSuccess {
                        Log.d("ProductDetailViewModel", "Vista de producto incrementada correctamente: $productId")
                    }
                    .onFailure { error ->
                        Log.e("ProductDetailViewModel", "Fallo al incrementar vista de producto: ${error.message}", error)
                    }

                val uid = getCurrentUserIdUseCase()
                getPublicProductUseCase(productId).getOrNull()?.let { product ->
                    if (!uid.isNullOrBlank()) {
                        trackInteractionUseCase(uid, product, InteractionType.VIEW)
                            .onSuccess {
                                Log.d("ProductDetailViewModel", "Interacción VIEW registrada para usuario: $uid")
                            }
                            .onFailure { error ->
                                Log.e("ProductDetailViewModel", "Fallo al registrar interacción VIEW: ${error.message}", error)
                            }
                    }
                }
            }

            // Cargar reseñas reales en tiempo real desde el use case de reviews
            launch {
                getReviewsUseCase(productId).collect { reviews ->
                    val comments = reviews.map { review ->
                        Comment(
                            id = review.id,
                            userName = review.studentName.ifBlank { "Estudiante UNMSM" },
                            rating = review.rating,
                            text = review.comment,
                            timestamp = review.createdAt
                        )
                    }
                    _state.update { it.copy(comments = comments) }
                }
            }

            getPublicProductUseCase(productId)
                .onSuccess { product ->
                    if (product != null) {
                        _state.update {
                            it.copy(
                                product = product,
                                status = ProductDetailStatus.Success
                            )
                        }
                    } else {
                        _state.update { it.copy(status = ProductDetailStatus.Error("Producto no encontrado")) }
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(status = ProductDetailStatus.Error(error.message ?: "Error al cargar producto")) }
                }
        }
    }

    private fun addComment() {
        val currentText = _state.value.newCommentText.trim()
        if (currentText.isBlank()) return

        val newComment = Comment(
            id = System.currentTimeMillis().toString(),
            userName = "Tú (Estudiante)",
            rating = _state.value.newCommentRating,
            text = currentText,
            timestamp = System.currentTimeMillis()
        )

        val updatedComments = listOf(newComment) + _state.value.comments
        _state.update {
            it.copy(
                comments = updatedComments,
                newCommentText = "",
                newCommentRating = 5
            )
        }
    }
}
