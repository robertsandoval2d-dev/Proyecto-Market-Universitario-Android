package com.example.marketuniversitario.feature.business.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import com.example.marketuniversitario.feature.business.domain.models.Business
import com.example.marketuniversitario.feature.business.domain.repositories.BusinessRepository
import com.example.marketuniversitario.feature.business.domain.repositories.BusinessStorageRepository
import com.example.marketuniversitario.feature.business.domain.repositories.ProductRepository
import javax.inject.Inject

class UpdateBusinessUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val businessRepository: BusinessRepository,
    private val productRepository: ProductRepository,
    private val storageRepository: BusinessStorageRepository
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

        // 1. Si el bannerUrl es una URI local (ej. content:// o file://), lo subimos a Firebase Storage
        var finalBannerUrl = business.bannerUrl
        if (!finalBannerUrl.isNullOrBlank() && !finalBannerUrl.startsWith("http")) {
            val uploadResult = storageRepository.uploadBusinessBanner(business.id, finalBannerUrl)
            finalBannerUrl = uploadResult.getOrElse { return Result.failure(it) }
        }

        val businessToUpdate = business.copy(bannerUrl = finalBannerUrl)

        // 2. Actualizar la información propia del negocio
        val updateBusinessResult = businessRepository.updateBusiness(businessToUpdate)
        if (updateBusinessResult.isFailure) {
            return updateBusinessResult
        }

        // 3. Actualizar en lote (batch) el nombre del negocio en todos sus productos/servicios asociados
        return productRepository.updateBusinessNameInProducts(business.id, business.name)
    }
}
