package com.example.marketuniversitario.feature.auth.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.entities.UserSession
import com.example.marketuniversitario.feature.auth.domain.exceptions.NotUniversityAccountException
import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase  @Inject constructor (
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(token: String): Result<UserSession> {

        val session = authRepository.signInWithGoogleToken(token)
            .getOrElse { return Result.failure(it) }

        if (!session.email.endsWith("@unmsm.edu.pe")) {
            authRepository.logout()
            return Result.failure(NotUniversityAccountException())
        }

        return Result.success(session)
    }

}
