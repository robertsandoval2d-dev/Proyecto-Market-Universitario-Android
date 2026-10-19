package com.example.marketuniversitario.feature.home.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import com.example.marketuniversitario.feature.business.domain.repositories.ProductRepository
import com.example.marketuniversitario.feature.home.data.mappers.toFeedProduct
import com.example.marketuniversitario.feature.home.domain.models.FeedProduct
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
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
        val productsList = productRepository.getProducts().map { products ->
            products
                .filter { product ->
                    val isNotOwnProduct =
                        userBusinessId.isNullOrBlank() || product.businessId != userBusinessId
                    val hasStock = (product.stock ?: 1) > 0
                    isNotOwnProduct && hasStock && product.isAvailable
                }
                .map { it.toFeedProduct() }
        }
        return productsList
    }
}