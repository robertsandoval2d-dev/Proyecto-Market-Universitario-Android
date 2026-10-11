package com.example.marketuniversitario.feature.business.ui.product_detail

import com.example.marketuniversitario.feature.business.domain.models.Product

data class Comment(
    val id: String = "",
    val userName: String = "",
    val userPhotoUrl: String? = null,
    val rating: Int = 5,
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

sealed interface ProductDetailStatus {
    object Idle : ProductDetailStatus
    object Loading : ProductDetailStatus
    object Success : ProductDetailStatus
    data class Error(val message: String) : ProductDetailStatus
}

data class ProductDetailState(
    val product: Product? = null,
    val comments: List<Comment> = emptyList(),
    val isFavorite: Boolean = false,
    val newCommentText: String = "",
    val newCommentRating: Int = 5,
    val status: ProductDetailStatus = ProductDetailStatus.Idle
)

sealed interface ProductDetailEvent {
    data class LoadProduct(val productId: String) : ProductDetailEvent
    data class NewCommentChanged(val text: String) : ProductDetailEvent
    data class NewRatingChanged(val rating: Int) : ProductDetailEvent
    object AddComment : ProductDetailEvent
    object ToggleFavorite : ProductDetailEvent
}
