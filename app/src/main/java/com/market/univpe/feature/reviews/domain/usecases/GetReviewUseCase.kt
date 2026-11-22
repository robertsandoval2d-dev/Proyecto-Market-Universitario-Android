package com.market.univpe.feature.reviews.domain.usecases

import com.market.univpe.feature.reviews.domain.models.Review
import com.market.univpe.feature.reviews.domain.repositories.ReviewRepository
import javax.inject.Inject

class GetReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(productId: String, studentId: String): Review? {
        return reviewRepository.getReview(productId, studentId).getOrNull()
    }
}
