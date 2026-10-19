package com.example.marketuniversitario.feature.user.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import com.example.marketuniversitario.feature.user.domain.model.User
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import javax.inject.Inject

class GetUserProfileInfoUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
){

    suspend operator fun invoke(): Result<User> {
        val uid = authRepository.getCurrentUserId() ?: return Result.failure(IllegalStateException("Sesión no disponible"))
        val user = userRepository.getUser(uid).fold(
            onSuccess = { user ->
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(
                        IllegalStateException("Usuario no encontrado")
                    )
                }
            },
            onFailure = { return Result.failure(it) }
        )
        return user
    }


}