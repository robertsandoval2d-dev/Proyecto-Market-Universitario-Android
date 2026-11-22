package com.market.univpe.feature.user.domain.usecases

import com.market.univpe.feature.user.domain.model.AccountStatus
import com.market.univpe.feature.user.domain.repositories.UserRepository
import javax.inject.Inject

class ApplyUserSanctionUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: String,
        status: AccountStatus,
        suspendedUntil: Long?,
        reason: String
    ): Result<Unit> {
        return userRepository.applyUserSanction(
            userId = userId,
            status = status,
            suspendedUntil = if (status == AccountStatus.BANNED) null else suspendedUntil,
            reason = reason.trim()
        )
    }
}
