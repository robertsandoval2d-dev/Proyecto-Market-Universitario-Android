package com.example.marketuniversitario.feature.business.domain.usecases

import com.example.marketuniversitario.feature.business.domain.models.Product
import com.example.marketuniversitario.feature.business.domain.repositories.ProductRepository
import javax.inject.Inject

class GetProductsByBusinessUseCase @Inject constructor(
    private val getMyBusinessUseCase: GetMyBusinessUseCase,
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(businessId: String? = null): Result<List<Product>> {
        val targetBusinessId = if (businessId.isNullOrBlank()) {
            val business = getMyBusinessUseCase().getOrElse { return Result.failure(it) }
                ?: return Result.failure(Exception("No se encontró un negocio asociado al usuario"))
            business.id
        } else {
            businessId
        }

        return productRepository.getProductsByBusiness(targetBusinessId)
    }
}
