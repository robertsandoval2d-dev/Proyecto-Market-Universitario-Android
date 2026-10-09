package com.example.marketuniversitario.feature.reviews.ui.rating

sealed interface RatingStatus {
    object Idle : RatingStatus
    object Loading : RatingStatus
    object Success : RatingStatus
    data class Error(val message: String) : RatingStatus
}

data class RatingState(
    val productId: String = "",
    val productName: String = "",
    val productImageUrl: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val isEditing: Boolean = false,
    val status: RatingStatus = RatingStatus.Idle
)

sealed interface RatingEvent {
    data class InitData(
        val productId: String,
        val studentId: String,
        val studentName: String,
        val productName: String,
        val productImageUrl: String
    ) : RatingEvent
    data class RatingChanged(val rating: Int) : RatingEvent
    data class CommentChanged(val comment: String) : RatingEvent
    object SubmitReview : RatingEvent
    object ResetStatus : RatingEvent
    object ResetForm : RatingEvent
}
