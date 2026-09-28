package com.example.marketuniversitario.feature.user.domain.usecases

import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import javax.inject.Inject

class CompleteUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: String,
        name: String,
        phone: String,
        faculty: String,
        gender: String,
        birthday: Long?,
        preferences: List<String>,
        primaryIntent: String,
        photoUrl: String?
    ): Result<Unit> {
        // Validaciones
        if (name.isBlank()) return Result.failure(Exception("El nombre es obligatorio"))
        if (phone.isBlank()) return Result.failure(Exception("El teléfono es obligatorio"))
        if (faculty.isBlank()) return Result.failure(Exception("Debes seleccionar una facultad"))

        val userResult = userRepository.getUser(userId)
        val currentUser = userResult.getOrNull()
            ?: return Result.failure(Exception("No se encontró la información base del usuario"))

        // Actualizar usuario
        val updatedUser = currentUser.copy(
            name = name.trim(),
            phone = phone.trim(),
            faculty = faculty,
            gender = gender,
            birthday = birthday,
            preferences = preferences,
            primaryIntent = primaryIntent,
            photoUrl = photoUrl,
            isProfileComplete = true
        )

        return userRepository.updateUserProfile(updatedUser)
    }
}