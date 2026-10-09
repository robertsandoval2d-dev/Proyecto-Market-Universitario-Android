package com.example.marketuniversitario.feature.reviews.domain.repositories

import com.example.marketuniversitario.feature.reviews.domain.models.Review
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun getReviews(productId: String): Flow<List<Review>>
    suspend fun getReview(productId: String, studentId: String): Result<Review?>
    suspend fun saveReview(productId: String, review: Review): Result<Unit>
}
