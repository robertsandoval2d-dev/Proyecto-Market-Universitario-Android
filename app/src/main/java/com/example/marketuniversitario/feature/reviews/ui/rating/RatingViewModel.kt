package com.example.marketuniversitario.feature.reviews.ui.rating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.reviews.domain.models.Review
import com.example.marketuniversitario.feature.reviews.domain.usecases.GetReviewUseCase
import com.example.marketuniversitario.feature.reviews.domain.usecases.SaveReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RatingViewModel @Inject constructor(
    private val saveReviewUseCase: SaveReviewUseCase,
    private val getReviewUseCase: GetReviewUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RatingState())
    val state = _state.asStateFlow()

    fun onEvent(event: RatingEvent) {
        when (event) {
            is RatingEvent.InitData -> {
                _state.update {
                    it.copy(
                        productId = event.productId,
                        studentId = event.studentId,
                        studentName = event.studentName,
                        productName = event.productName,
                        productImageUrl = event.productImageUrl
                    )
                }
                loadExistingReview(event.productId, event.studentId)
            }
            is RatingEvent.RatingChanged -> {
                _state.update { it.copy(rating = event.rating) }
            }
            is RatingEvent.CommentChanged -> {
                if (event.comment.length <= 200) {
                    _state.update { it.copy(comment = event.comment) }
                }
            }
            is RatingEvent.SubmitReview -> {
                submitReview()
            }
            is RatingEvent.ResetStatus -> {
                _state.update { it.copy(status = RatingStatus.Idle) }
            }
            is RatingEvent.ResetForm -> {
                _state.update { RatingState() }
            }
        }
    }

    private fun loadExistingReview(productId: String, studentId: String) {
        if (studentId.isBlank() || productId.isBlank()) return
        viewModelScope.launch {
            val review = getReviewUseCase(productId, studentId)
            if (review != null) {
                _state.update {
                    it.copy(
                        rating = review.rating,
                        comment = review.comment,
                        isEditing = true
                    )
                }
            }
        }
    }

    private fun submitReview() {
        val currentState = _state.value
        if (currentState.rating <= 0 || currentState.productId.isBlank() || currentState.studentId.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(status = RatingStatus.Loading) }
            val review = Review(
                id = currentState.studentId,
                studentId = currentState.studentId,
                studentName = currentState.studentName,
                rating = currentState.rating,
                comment = currentState.comment.trim(),
                createdAt = System.currentTimeMillis()
            )

            saveReviewUseCase(currentState.productId, review)
                .onSuccess {
                    _state.update { it.copy(status = RatingStatus.Success) }
                }
                .onFailure { error ->
                    _state.update { it.copy(status = RatingStatus.Error(error.message ?: "Error al guardar calificación")) }
                }
        }
    }
}
