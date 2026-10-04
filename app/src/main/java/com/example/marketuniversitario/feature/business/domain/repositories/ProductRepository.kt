package com.example.marketuniversitario.feature.business.domain.repositories

import com.example.marketuniversitario.feature.business.domain.models.Product

interface ProductRepository {
    suspend fun createProduct(product: Product): Result<Product>
    suspend fun getProduct(productId: String): Result<Product?>
    suspend fun getProductsByBusiness(businessId: String): Result<List<Product>>
    suspend fun getProducts(): Result<List<Product>>
    suspend fun updateProduct(product: Product): Result<Unit>
    suspend fun updateBusinessNameInProducts(businessId: String, newBusinessName: String): Result<Unit>
    suspend fun deleteProduct(productId: String): Result<Unit>
}
