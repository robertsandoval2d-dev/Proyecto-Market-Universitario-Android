package com.example.marketuniversitario.feature.user.domain.repositories

interface StorageRepository {
    suspend fun uploadProfileImage(userId: String, imageUri: String): Result<String>
}