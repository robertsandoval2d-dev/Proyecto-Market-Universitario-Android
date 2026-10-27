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
        // 1. Tags semilla: affinityScores manda, preferences cubre el arranque en frío
        val seedTags = buildSeedTags(user)

        // 2. Traer hasta 30 candidatos pre-filtrados de Firestore
        val candidates = productRepository.getCandidateProducts(seedTags)
            .getOrElse { return Result.failure(it) }

        if (candidates.isEmpty()) return Result.success(emptyList())

        // 3. Ordenamiento semántico con Gemini (el repositorio maneja caché y fallback)
        return aiRecommendationRepository.getRankedProductIds(user, candidates)
    }

    private fun buildSeedTags(user: User): List<String> {
        val scores = HashMap<String, Double>()
        user.affinityScores.forEach { (tag, score) -> scores[tag] = score.toDouble() }

        // Preferences suman un peso base: dominan sin historial y se diluyen
        // cuando el usuario ya acumuló puntos reales.
        user.preferences.forEach { tag -> scores.merge(tag, PREFERENCE_WEIGHT, Double::plus) }

        // Una búsqueda reciente refuerza un tag que ya existe
        val searchTokens = user.recentSearches
            .takeLast(MAX_RECENT_SEARCHES)
            .flatMap { it.lowercase().split(" ") }
            .filter { it.length >= 3 }
            .toSet()
        scores.keys.toList().forEach { tag ->
            if (tag.lowercase() in searchTokens) scores.merge(tag, SEARCH_BOOST, Double::plus)
        }

        return scores.entries
            .sortedByDescending { it.value }
            .take(MAX_SEED_TAGS) // Firestore: whereArrayContainsAny admite máx. 10
            .map { it.key }
    }

    private companion object {
        const val MAX_SEED_TAGS = 10
        const val MAX_RECENT_SEARCHES = 5
        const val PREFERENCE_WEIGHT = 3.0 // ≈ 3 puntos de afinidad
        const val SEARCH_BOOST = 2.0
    }
}