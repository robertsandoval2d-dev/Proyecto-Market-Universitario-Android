package com.market.univpe.feature.reviews.domain.usecases

import com.market.univpe.feature.reviews.domain.models.Review
import com.market.univpe.feature.reviews.domain.repositories.ReviewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReviewsUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    operator fun invoke(productId: String): Flow<List<Review>> {
        return reviewRepository.getReviews(productId)
    }
}
