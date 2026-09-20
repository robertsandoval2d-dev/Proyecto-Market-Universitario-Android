package com.example.marketuniversitario.feature.auth.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.models.UserSession
import com.example.marketuniversitario.feature.auth.domain.exceptions.AuthException
import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(token: String): Result<UserSession> {

        val session = authRepository.signInWithGoogleToken(token)
            .getOrElse { return Result.failure(it) }

        if (!session.email.endsWith("@unmsm.edu.pe")) {
            authRepository.logout()
            return Result.failure(AuthException.NotUniversityAccount)
        }

        return Result.success(session)
    }

}
