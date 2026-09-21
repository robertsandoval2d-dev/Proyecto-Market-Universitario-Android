package com.example.marketuniversitario.feature.auth.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.models.UserSession
import com.example.marketuniversitario.feature.auth.domain.exceptions.AuthException
import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<UserSession> {

        val allowedDomain = "@unmsm.edu.pe"

        if (!email.endsWith(allowedDomain)) {
            return Result.failure(AuthException.NotUniversityAccount)
        }

        if (password.length < 6) {
            return Result.failure(AuthException.WeakPassword)
        }

        val session = authRepository.register(email, password)
            .getOrElse { return Result.failure(it) }

        authRepository.sendEmailVerification()
            .onFailure {
                return Result.failure(it)
            }

        return Result.success(session.copy(isEmailVerified = false))
    }
}