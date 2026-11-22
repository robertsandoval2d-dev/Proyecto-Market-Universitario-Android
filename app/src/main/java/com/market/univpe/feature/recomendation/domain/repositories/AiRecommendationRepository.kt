package com.market.univpe.feature.recomendation.domain.repositories

import com.market.univpe.feature.business.domain.models.Product
import com.market.univpe.feature.user.domain.model.User

interface AiRecommendationRepository {
    suspend fun getRankedProductIds(user: User, candidates: List<Product>): Result<List<String>>
}
