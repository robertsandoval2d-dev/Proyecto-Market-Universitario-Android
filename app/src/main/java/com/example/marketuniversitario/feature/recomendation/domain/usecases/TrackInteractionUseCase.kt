package com.example.marketuniversitario.feature.recomendation.domain.usecases

import com.example.marketuniversitario.feature.business.domain.models.Product
import com.example.marketuniversitario.feature.recomendation.domain.models.InteractionType
import com.example.marketuniversitario.feature.recomendation.domain.repositories.InteractionRepository
import javax.inject.Inject

class TrackInteractionUseCase @Inject constructor(
    private val interactionRepository: InteractionRepository
) {
    suspend operator fun invoke(userId: String, product: Product, type: InteractionType): Result<Unit> {
        val tagsToUpdate = (listOf(product.category) + product.tags).filter { it.isNotBlank() }
        if (tagsToUpdate.isEmpty()) return Result.success(Unit)
        return interactionRepository.incrementAffinityScores(userId, tagsToUpdate, type.weight)
    }
}
