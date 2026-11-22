package com.market.univpe.feature.auth.domain.usecases

import com.market.univpe.feature.auth.domain.models.UserSession
import com.market.univpe.feature.auth.domain.exceptions.AuthException
import com.market.univpe.feature.auth.domain.repositories.AuthRepository
import com.market.univpe.feature.user.domain.model.User
import com.market.univpe.feature.user.domain.repositories.UserRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(token: String): Result<UserSession> {

        val session = authRepository.signInWithGoogleToken(token)
            .getOrElse { return Result.failure(it) }

        if (!session.email.endsWith("@unmsm.edu.pe")) {
            authRepository.logout()
            return Result.failure(AuthException.NotUniversityAccount)
        }

        val existingUser = userRepository.getUser(session.uid).getOrNull()
        if (existingUser == null) {
            val newUser = User(
                id = session.uid,
                name = session.displayName.orEmpty(),
                email = session.email,
                hasBusiness = false,
                businessId = null,
                photoUrl = session.photoUrl
            )
            userRepository.saveUser(newUser)
                .onFailure {
                    authRepository.logout()
                    return Result.failure(it)
                }
        }

        return Result.success(session)
    }

}
