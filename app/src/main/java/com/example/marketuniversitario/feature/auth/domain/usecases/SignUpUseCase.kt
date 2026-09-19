package com.example.marketuniversitario.feature.auth.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.entities.UserSession
import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<UserSession> {

        val allowedDomain = "@unmsm.edu.pe"

        if (!email.endsWith(allowedDomain)) {
            return Result.failure(Exception("Formato de correo inválido"))
        }

        if (password.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres."))
        }

        val session = authRepository.register(email,password)
            .getOrElse { return Result.failure(it) }

        authRepository.sendEmailVerification()
            .onFailure {
                return Result.success(session.copy(isEmailVerified = false))
            }

        return Result.success(session)
    }
}