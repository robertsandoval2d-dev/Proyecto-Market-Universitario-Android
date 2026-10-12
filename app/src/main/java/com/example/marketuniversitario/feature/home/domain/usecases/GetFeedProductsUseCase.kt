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
            val currentUserId = authRepository.getCurrentUserId()
            val userBusinessId = if (currentUserId != null) {
                userRepository.getUser(currentUserId).getOrNull()?.businessId
            } else {
                null
            }

            val productsList = productRepository.getProducts().getOrElse { emptyList() }

            val filteredProducts = productsList.filter { product ->
                val isNotOwnProduct = userBusinessId.isNullOrBlank() || product.businessId != userBusinessId
                val hasStock = product.stock == null || product.stock > 0
                val isAvailable = product.isAvailable

                isNotOwnProduct && hasStock && isAvailable
            }

            val feedProducts = filteredProducts.map { product ->
                product.toFeedProduct()
            }

            Result.success(feedProducts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}