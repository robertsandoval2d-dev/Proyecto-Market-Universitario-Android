package com.example.marketuniversitario.feature.recomendation.domain.usecases

import com.example.marketuniversitario.feature.business.domain.models.Product
import com.example.marketuniversitario.feature.recomendation.domain.repositories.AiRecommendationRepository
import com.example.marketuniversitario.feature.user.domain.model.User
import javax.inject.Inject

class GetAiRecommendationsUseCase @Inject constructor(
    private val aiRecommendationRepository: AiRecommendationRepository
) {
    suspend operator fun invoke(user: User, candidates: List<Product>): Result<List<String>> {
        return aiRecommendationRepository.getRankedProductIds(user, candidates)
    }
}
