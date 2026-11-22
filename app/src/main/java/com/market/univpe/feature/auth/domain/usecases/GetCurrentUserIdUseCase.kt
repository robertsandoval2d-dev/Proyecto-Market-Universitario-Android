package com.market.univpe.feature.auth.domain.usecases

import com.market.univpe.feature.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class GetCurrentUserIdUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): String? {
        return authRepository.getCurrentUserId()
    }
}
