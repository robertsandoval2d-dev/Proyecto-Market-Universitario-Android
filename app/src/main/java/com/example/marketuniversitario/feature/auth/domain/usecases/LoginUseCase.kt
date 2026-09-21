package com.example.marketuniversitario.feature.auth.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.exceptions.AuthException
import com.example.marketuniversitario.feature.auth.domain.models.UserSession
import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import com.example.marketuniversitario.feature.user.domain.model.User
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<UserSession> {

        if (!email.endsWith("@unmsm.edu.pe")) {
            return Result.failure(AuthException.NotUniversityAccount)
        }

        val session = authRepository.loginWithEmail(email, password)
            .getOrElse { return Result.failure(it) }

        val reloadedSession = authRepository.reloadUser()
            .getOrElse { session }

        if (!reloadedSession.isEmailVerified) {
            authRepository.logout()
            return Result.failure(AuthException.NotVerifiedEmail)
        }

        val existingUser = userRepository.getUser(reloadedSession.uid).getOrNull()
        if (existingUser == null) {
            val newUser = User(
                id = reloadedSession.uid,
                email = reloadedSession.email,
                hasBusiness = false,
                businessId = null
            )
            userRepository.saveUser(newUser)
                .onFailure {
                    authRepository.logout()
                    return Result.failure(it)
                }
        }

        return Result.success(reloadedSession)
    }
}