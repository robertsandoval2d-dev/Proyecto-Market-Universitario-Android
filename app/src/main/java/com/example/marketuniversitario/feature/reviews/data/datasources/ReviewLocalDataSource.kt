package com.example.marketuniversitario.feature.reviews.data.datasources

import com.example.marketuniversitario.feature.reviews.data.models.ReviewRoomEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReviewLocalDataSource @Inject constructor(
    private val reviewDao: ReviewDao
) {
    fun getReviews(productId: String): Flow<List<ReviewRoomEntity>> {
        return reviewDao.getReviews(productId)
    }

    suspend fun getReviewByUser(productId: String, studentId: String): ReviewRoomEntity? {
        return reviewDao.getReviewByUser(productId, studentId)
    }

    suspend fun getUnsyncedReviews(): List<ReviewRoomEntity> {
        return reviewDao.getUnsyncedReviews()
    }

    suspend fun markAsSynced(reviewId: String) {
        reviewDao.markAsSynced(reviewId)
    }

    suspend fun insertReviews(reviews: List<ReviewRoomEntity>) {
        reviewDao.insertReviews(reviews)
    }

    suspend fun insertReview(review: ReviewRoomEntity) {
        reviewDao.insertReview(review)
    }
}
