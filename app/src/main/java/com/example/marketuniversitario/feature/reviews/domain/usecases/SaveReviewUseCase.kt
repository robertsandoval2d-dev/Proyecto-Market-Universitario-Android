package com.example.marketuniversitario.feature.reviews.domain.usecases

import com.example.marketuniversitario.feature.business.domain.repositories.BusinessRepository
import com.example.marketuniversitario.feature.business.domain.repositories.ProductRepository
import com.example.marketuniversitario.feature.reviews.domain.models.Review
import com.example.marketuniversitario.feature.reviews.domain.repositories.ReviewRepository
import java.util.UUID
import javax.inject.Inject

class SaveReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val productRepository: ProductRepository,
    private val businessRepository: BusinessRepository
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

        val result = reviewRepository.saveReview(productId, reviewToSave)

        // Solución Arquitectónica: Actualizar las métricas del negocio en cascada (NoSQL approach)
        if (result.isSuccess) {
            val product = productRepository.getProduct(productId).getOrNull()
            if (product != null && product.businessId.isNotBlank()) {
                val business = businessRepository.getBusiness(product.businessId).getOrNull()
                if (business != null) {
                    val newTotalReviews = business.totalReviews + 1
                    val newRating = ((business.rating * business.totalReviews) + review.rating) / newTotalReviews

                    businessRepository.updateBusinessRemote(
                        business.copy(
                            totalReviews = newTotalReviews,
                            rating = newRating
                        )
                    )
                }
            }
        }

        return result
    }
}
