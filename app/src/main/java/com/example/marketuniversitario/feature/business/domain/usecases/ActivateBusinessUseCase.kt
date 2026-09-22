package com.example.marketuniversitario.feature.business.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import com.example.marketuniversitario.feature.business.domain.models.Business
import com.example.marketuniversitario.feature.business.domain.repositories.BusinessRepository
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import javax.inject.Inject

class ActivateBusinessUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val businessRepository: BusinessRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        name: String,
        description: String = "",
        category: String = ""
    ): Result<Business> {
        val currentUid = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("Usuario no autenticado"))

        if (name.isBlank()) {
            return Result.failure(Exception("El nombre del negocio no puede estar vacío"))
        }

        val newBusiness = Business(
            ownerId = currentUid,
            name = name.trim(),
            description = description.trim(),
            category = category.trim(),
            isActive = true
        )

        val createdBusiness = businessRepository.createBusiness(newBusiness)
            .getOrElse { return Result.failure(it) }

        userRepository.updateUserBusinessStatus(
            userId = currentUid,
            hasBusiness = true,
            businessId = createdBusiness.id
        ).onFailure { return Result.failure(it) }

        return Result.success(createdBusiness)
    }
}
