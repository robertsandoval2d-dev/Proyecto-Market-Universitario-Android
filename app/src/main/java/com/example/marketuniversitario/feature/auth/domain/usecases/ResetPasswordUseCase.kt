package com.example.marketuniversitario.feature.auth.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Boolean> {
        if (email.isBlank()) {
            return Result.failure(Exception("El correo no puede estar vacío."))
        }

        if (!email.contains("@unmsm.edu.pe")) {
            return Result.failure(Exception("Debe ser un correo institucional válido."))
        }

        return authRepository.sendPasswordResetEmail(email)
    }
}