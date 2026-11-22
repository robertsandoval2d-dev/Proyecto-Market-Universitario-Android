package com.market.univpe.feature.business.domain.usecases

import com.market.univpe.feature.auth.domain.repositories.AuthRepository
import com.market.univpe.feature.business.domain.models.Business
import com.market.univpe.feature.business.domain.repositories.BusinessRepository
import javax.inject.Inject

class UpdateBusinessUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val businessRepository: BusinessRepository
) {
    suspend operator fun invoke(business: Business): Result<Unit> {
        val currentUid = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("Usuario no autenticado"))

        if (business.ownerId != currentUid) {
            return Result.failure(Exception("No tienes permisos para editar este negocio"))
        }

        if (business.name.isBlank()) {
            return Result.failure(Exception("El nombre del negocio no puede estar vacío"))
        }

        return businessRepository.updateBusiness(business)
    }
}
