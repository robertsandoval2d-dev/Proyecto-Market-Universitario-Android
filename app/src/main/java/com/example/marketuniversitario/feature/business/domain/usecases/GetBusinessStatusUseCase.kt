package com.example.marketuniversitario.feature.business.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import javax.inject.Inject

class GetBusinessStatusUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        val currentUid = authRepository.getCurrentUserId()
            ?: return Result.success(false)

        return userRepository.getUser(currentUid)
            .map { user -> user?.hasBusiness == true }
    }
}
