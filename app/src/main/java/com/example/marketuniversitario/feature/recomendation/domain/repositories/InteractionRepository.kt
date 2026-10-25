package com.example.marketuniversitario.feature.recomendation.domain.repositories

interface InteractionRepository {
    suspend fun incrementAffinityScores(userId: String, tags: List<String>, weight: Int): Result<Unit>
}
