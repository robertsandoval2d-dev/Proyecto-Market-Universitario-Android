package com.example.marketuniversitario.feature.business.domain.usecases

import com.example.marketuniversitario.feature.business.domain.repositories.ProductRepository
import javax.inject.Inject

class DeleteProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: String): Result<Unit> {
        if (productId.isBlank()) {
            return Result.failure(Exception("ID de producto inválido"))
        }
        return productRepository.deleteProduct(productId)
    }
}
