package com.example.marketuniversitario.feature.auth.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.models.UserSession
import com.example.marketuniversitario.feature.auth.domain.exceptions.AuthException
import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<UserSession> {

        if (!email.endsWith("@unmsm.edu.pe")) {
            return Result.failure(AuthException.NotUniversityAccount)
        }

        val session = authRepository.loginWithEmail(email, password)
            .getOrElse { return Result.failure(it) }

        if (!session.isEmailVerified) {
            authRepository.logout()
            return Result.failure(Exception("Debes verificar tu correo institucional antes de ingresar."))
        }

        return Result.success(session)
    }
}