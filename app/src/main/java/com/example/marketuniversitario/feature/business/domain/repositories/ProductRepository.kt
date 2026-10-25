package com.example.marketuniversitario.feature.business.domain.repositories

import com.example.marketuniversitario.feature.business.domain.models.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun createProduct(product: Product): Result<Product>
    suspend fun getProduct(productId: String): Result<Product?>
    suspend fun getPublicProductInfo(productId: String): Result<Product?>
    suspend fun getProductsByBusiness(businessId: String): Result<List<Product>>
    suspend fun getPublicProductsByBusiness(businessId: String): Result<List<Product>>
    suspend fun getCandidateProducts(topTags: List<String>): Result<List<Product>>
    fun getProducts(): Flow<List<Product>>
    suspend fun updateProduct(product: Product): Result<Unit>
    suspend fun updateBusinessNameInProducts(businessId: String, newBusinessName: String): Result<Unit>
    suspend fun incrementViews(productId: String): Result<Unit>
    suspend fun incrementSalesCount(productId: String): Result<Unit>
    suspend fun releaseReservedStock(productId: String, quantity: Int): Result<Unit>
    suspend fun confirmReservedStock(productId: String, quantity: Int): Result<Unit>
    suspend fun deleteProduct(productId: String): Result<Unit>
}
