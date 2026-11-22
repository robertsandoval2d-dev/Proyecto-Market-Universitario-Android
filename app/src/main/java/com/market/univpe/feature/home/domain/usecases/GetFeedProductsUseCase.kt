package com.market.univpe.feature.home.domain.usecases

import com.market.univpe.feature.auth.domain.repositories.AuthRepository
import com.market.univpe.feature.business.domain.repositories.ProductRepository
import com.market.univpe.feature.home.data.mappers.toFeedProduct
import com.market.univpe.feature.home.domain.models.FeedProduct
import com.market.univpe.feature.user.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFeedProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Flow<List<FeedProduct>> {
        val currentUserId = authRepository.getCurrentUserId()
            ?: throw IllegalStateException("Sesión no disponible")
        val userBusinessId = userRepository.getUser(currentUserId).getOrThrow()?.businessId

        return productRepository.getProducts().map { products ->
            products
                .filter { product ->
                    val isNotOwnProduct =
                        userBusinessId.isNullOrBlank() || product.businessId != userBusinessId
                    val hasStock = (product.stock ?: 1) > 0
                    isNotOwnProduct && hasStock && product.isAvailable
                }
                .map { it.toFeedProduct() }
        }
    }
}