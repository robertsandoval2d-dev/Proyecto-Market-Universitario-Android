package com.example.marketuniversitario.feature.business.domain.repositories

interface BusinessStorageRepository {
    suspend fun uploadBusinessBanner(businessId: String, imageUri: String): Result<String>
    suspend fun uploadProductImage(productId: String, imageUri: String): Result<String>
}
