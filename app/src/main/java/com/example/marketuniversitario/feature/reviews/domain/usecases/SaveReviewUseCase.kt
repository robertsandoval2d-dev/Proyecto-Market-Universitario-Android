package com.example.marketuniversitario.feature.reviews.domain.usecases

import com.example.marketuniversitario.feature.reviews.domain.models.Review
import com.example.marketuniversitario.feature.reviews.domain.repositories.ReviewRepository
import java.util.UUID
import javax.inject.Inject

class SaveReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(productId: String, review: Review): Result<Unit> {
        if (review.rating !in 1..5) {
            return Result.failure(IllegalArgumentException("La valoración debe ser entre 1 y 5 estrellas."))
        }

        val docId = if (review.studentId.isNotBlank()) {
            review.studentId
        } else if (review.id.isNotBlank()) {
            review.id
        } else {
            UUID.randomUUID().toString()
        }

        val reviewToSave = review.copy(
            id = docId,
            createdAt = if (review.createdAt == 0L) System.currentTimeMillis() else review.createdAt
        )

        return reviewRepository.saveReview(productId, reviewToSave)
    }
}
