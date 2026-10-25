package com.example.marketuniversitario.feature.recomendation.domain.usecases

import com.example.marketuniversitario.feature.business.domain.repositories.ProductRepository
import com.example.marketuniversitario.feature.recomendation.domain.repositories.AiRecommendationRepository
import com.example.marketuniversitario.feature.user.domain.model.User
import javax.inject.Inject

class GetPersonalizedFeedUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val aiRecommendationRepository: AiRecommendationRepository
) {
    suspend operator fun invoke(user: User): Result<List<String>> {
        // 1. Extraer los tags con mayor puntuación
        val topTags = user.affinityScores.entries
            .sortedByDescending { it.value }
            .take(5)
            .map { it.key }

        // 2. Traer 30 candidatos pre-filtrados de Firestore
        val candidatesResult = productRepository.getCandidateProducts(topTags)
        val candidates = candidatesResult.getOrDefault(emptyList())

        if (candidates.isEmpty()) return Result.success(emptyList())

        // 3. Enviar el lote a Gemini para el ordenamiento semántico preciso
        return aiRecommendationRepository.getRankedProductIds(user, candidates)
    }
}
