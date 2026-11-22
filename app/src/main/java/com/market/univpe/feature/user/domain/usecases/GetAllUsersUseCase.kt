package com.market.univpe.feature.user.domain.usecases

import com.market.univpe.feature.user.domain.model.User
import com.market.univpe.feature.user.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<User>> {
        return userRepository.getAllUsers()
    }
}
