package com.example.marketuniversitario.feature.recomendation.domain.repositories

import com.example.marketuniversitario.feature.business.domain.models.Product
import com.example.marketuniversitario.feature.user.domain.model.User

interface AiRecommendationRepository {
    suspend fun getRankedProductIds(user: User, candidates: List<Product>): Result<List<String>>
}
