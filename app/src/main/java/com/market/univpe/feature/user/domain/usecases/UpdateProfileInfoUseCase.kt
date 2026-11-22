package com.market.univpe.feature.user.domain.usecases

import com.market.univpe.feature.user.domain.model.User
import com.market.univpe.feature.user.domain.repositories.UserRepository
import javax.inject.Inject


class UpdateProfileInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User): Result<Unit> {
        return userRepository.updateUserProfile(user)
    }
}