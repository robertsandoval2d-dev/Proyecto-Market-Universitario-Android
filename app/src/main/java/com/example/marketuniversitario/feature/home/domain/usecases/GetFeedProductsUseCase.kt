package com.example.marketuniversitario.feature.home.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import com.example.marketuniversitario.feature.business.domain.repositories.ProductRepository
import com.example.marketuniversitario.feature.home.data.mappers.toFeedProduct
import com.example.marketuniversitario.feature.home.domain.models.FeedProduct
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import javax.inject.Inject

class GetFeedProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<List<FeedProduct>> {
        return try {
            val currentUserId = authRepository.getCurrentUserId() ?: return Result.failure(IllegalStateException("Sesión no disponible"))
            val userBusinessId = userRepository.getUser(currentUserId).getOrThrow()?.businessId

            val productsList = productRepository.getProducts().getOrThrow()

            val filteredProducts = productsList.filter { product ->
                val isNotOwnProduct = userBusinessId.isNullOrBlank() || product.businessId != userBusinessId
                val hasStock = product.stock == null || product.stock > 0
                val isAvailable = product.isAvailable

                isNotOwnProduct && hasStock && isAvailable
            }

            Result.success(filteredProducts.map { it.toFeedProduct() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}