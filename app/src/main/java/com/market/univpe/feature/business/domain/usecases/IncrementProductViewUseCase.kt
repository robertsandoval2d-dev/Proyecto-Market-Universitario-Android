package com.market.univpe.feature.business.domain.usecases

import com.market.univpe.feature.business.domain.repositories.ProductRepository
import javax.inject.Inject

class IncrementProductViewUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: String): Result<Unit> {
        return productRepository.incrementViews(productId)
    }
}
