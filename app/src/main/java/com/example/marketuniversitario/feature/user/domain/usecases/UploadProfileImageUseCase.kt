package com.example.marketuniversitario.feature.user.domain.usecases

import com.example.marketuniversitario.feature.user.domain.repositories.StorageRepository
import javax.inject.Inject

class UploadProfileImageUseCase @Inject constructor(
    private val storageRepository: StorageRepository
) {
    suspend operator fun invoke(userId: String, imageUri: String): Result<String> {
        if (imageUri.isBlank()) return Result.failure(Exception("URI de imagen inválida"))
        return storageRepository.uploadProfileImage(userId, imageUri)
    }
}