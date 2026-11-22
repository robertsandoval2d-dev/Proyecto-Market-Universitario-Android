package com.market.univpe.feature.user.domain.usecases

import com.market.univpe.feature.auth.domain.repositories.AuthRepository
import com.market.univpe.feature.user.domain.model.User
import com.market.univpe.feature.user.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class ObserveUserProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<User?> {
        val uid = authRepository.getCurrentUserId() ?: return emptyFlow()
        return userRepository.getUserStream(uid)
    }
}
