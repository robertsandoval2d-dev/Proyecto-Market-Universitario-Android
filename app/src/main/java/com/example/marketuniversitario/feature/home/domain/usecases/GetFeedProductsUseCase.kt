package com.example.marketuniversitario.feature.home.domain.usecases

import com.example.marketuniversitario.feature.business.domain.repositories.ProductRepository
import com.example.marketuniversitario.feature.home.data.toFeedProduct
import com.example.marketuniversitario.feature.home.domain.models.FeedProduct
import javax.inject.Inject

class GetFeedProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
){
    suspend operator fun invoke(): Result<List<FeedProduct>> {
        val result = productRepository.getProducts()

        return result.fold(
            onSuccess = { productsList ->
                val feedProducts = productsList.map { product ->
                    product.toFeedProduct()
                }
                Result.success(feedProducts)
            },
            onFailure = {error ->
                Result.failure(error)
            }
        )
    }
}