package com.example.marketuniversitario.feature.user.domain.usecases

import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import javax.inject.Inject

class RestoreUserAccountUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return userRepository.restoreUserAccount(userId)
    }
}
