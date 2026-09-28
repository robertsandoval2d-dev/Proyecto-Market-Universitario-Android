package com.example.marketuniversitario.feature.business.domain.usecases

import com.example.marketuniversitario.feature.business.domain.models.Product
import com.example.marketuniversitario.feature.business.domain.repositories.ProductRepository
import javax.inject.Inject

class GetProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: String): Result<Product?> {
        if (productId.isBlank()) {
            return Result.failure(Exception("ID de producto inválido"))
        }
        return productRepository.getProduct(productId)
    }
}
