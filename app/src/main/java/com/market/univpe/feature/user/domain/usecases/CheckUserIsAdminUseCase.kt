package com.market.univpe.feature.user.domain.usecases

import com.market.univpe.feature.auth.domain.repositories.AuthRepository
import com.market.univpe.feature.user.domain.repositories.UserRepository
import javax.inject.Inject

class CheckUserIsAdminUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        val uid = authRepository.getCurrentUserId()
            ?: return Result.success(false)

        val user = userRepository.getUser(uid).getOrNull()

        return Result.success(user?.isAdmin == true)
    }
}
