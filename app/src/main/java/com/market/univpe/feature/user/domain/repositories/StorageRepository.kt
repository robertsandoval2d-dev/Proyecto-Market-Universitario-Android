package com.market.univpe.feature.user.domain.repositories

interface StorageRepository {
    suspend fun uploadProfileImage(userId: String, imageUri: String): Result<String>
}