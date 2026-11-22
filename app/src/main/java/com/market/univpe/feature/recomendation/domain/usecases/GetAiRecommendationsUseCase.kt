package com.market.univpe.feature.recomendation.domain.usecases

import com.market.univpe.feature.business.domain.models.Product
import com.market.univpe.feature.recomendation.domain.repositories.AiRecommendationRepository
import com.market.univpe.feature.user.domain.model.User
import javax.inject.Inject

class GetAiRecommendationsUseCase @Inject constructor(
    private val aiRecommendationRepository: AiRecommendationRepository
) {
    suspend operator fun invoke(user: User, candidates: List<Product>): Result<List<String>> {
        return aiRecommendationRepository.getRankedProductIds(user, candidates)
    }
}
