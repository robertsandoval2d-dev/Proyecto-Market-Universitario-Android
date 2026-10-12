package com.example.marketuniversitario.feature.business.domain.usecases

import com.example.marketuniversitario.feature.business.domain.models.Product
import com.example.marketuniversitario.feature.business.domain.repositories.ProductRepository
import javax.inject.Inject

class GetPublicProductsByBusinessUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(businessId: String): Result<List<Product>> {
        if (businessId.isBlank()) {
            return Result.failure(Exception("ID de negocio inválido"))
        }
        return productRepository.getPublicProductsByBusiness(businessId)
    }
}
