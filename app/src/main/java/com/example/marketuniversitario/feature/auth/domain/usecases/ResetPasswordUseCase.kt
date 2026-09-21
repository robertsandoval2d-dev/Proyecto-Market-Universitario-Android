package com.example.marketuniversitario.feature.auth.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.exceptions.AuthException
import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Boolean> {
        if (email.isBlank()) {
            return Result.failure(Exception("El correo no puede estar vacío."))
        }

        if (!email.endsWith("@unmsm.edu.pe")) {
            return Result.failure(AuthException.NotUniversityAccount)
        }

        return authRepository.sendPasswordResetEmail(email)
    }
}