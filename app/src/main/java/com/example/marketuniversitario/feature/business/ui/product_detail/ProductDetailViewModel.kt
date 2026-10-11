package com.example.marketuniversitario.feature.business.ui.product_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.business.domain.usecases.GetProductUseCase
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
    private val getProductUseCase: GetProductUseCase,
    private val getReviewsUseCase: GetReviewsUseCase,
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

            getProductUseCase(productId)
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

    private fun mockComments(): List<Comment> {
        return listOf(
            Comment(
                id = "c1",
                userName = "Sebastián P.",
                rating = 5,
                text = "¡Excelente producto! Me salvó para el examen, recomendado 100%.",
                timestamp = System.currentTimeMillis() - 86400000L
            ),
            Comment(
                id = "c2",
                userName = "María G.",
                rating = 4,
                text = "Muy buena atención del vendedor y la entrega fue rápida en la facultad.",
                timestamp = System.currentTimeMillis() - 172800000L
            )
        )
    }
}
