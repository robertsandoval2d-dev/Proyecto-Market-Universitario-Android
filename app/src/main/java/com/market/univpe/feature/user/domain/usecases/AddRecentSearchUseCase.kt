package com.market.univpe.feature.user.domain.usecases

import com.market.univpe.feature.user.domain.repositories.UserRepository
import javax.inject.Inject

class AddRecentSearchUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String, searchQuery: String): Result<Unit> {
        return userRepository.addRecentSearch(userId, searchQuery)
    }
}
