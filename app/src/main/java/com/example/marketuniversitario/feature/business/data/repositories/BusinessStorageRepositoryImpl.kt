package com.example.marketuniversitario.feature.business.data.repositories

import com.example.marketuniversitario.feature.business.data.datasources.BusinessStorageRemoteDataSource
import com.example.marketuniversitario.feature.business.domain.repositories.BusinessStorageRepository
import javax.inject.Inject

class BusinessStorageRepositoryImpl @Inject constructor(
    private val remoteDataSource: BusinessStorageRemoteDataSource
) : BusinessStorageRepository {

    override suspend fun uploadBusinessBanner(businessId: String, imageUri: String): Result<String> {
        return try {
            val url = remoteDataSource.uploadImage("business_banners/$businessId.webp", imageUri)
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadProductImage(productId: String, imageUri: String): Result<String> {
        return try {
            val url = remoteDataSource.uploadImage("product_images/$productId-${System.currentTimeMillis()}.webp", imageUri)
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
