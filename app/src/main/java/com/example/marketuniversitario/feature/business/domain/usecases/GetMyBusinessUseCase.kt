package com.example.marketuniversitario.feature.business.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import com.example.marketuniversitario.feature.business.domain.models.Business
import com.example.marketuniversitario.feature.business.domain.repositories.BusinessRepository
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import javax.inject.Inject

class GetMyBusinessUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val businessRepository: BusinessRepository
) {
    suspend operator fun invoke(): Result<Business?> {
        val currentUid = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("Usuario no autenticado"))

        val user = userRepository.getUser(currentUid)
            .getOrElse { return Result.failure(it) }

        if (user == null || !user.hasBusiness || user.businessId.isNullOrBlank()) {
            return Result.success(null)
        }

        return businessRepository.getBusiness(user.businessId)
    }
}
