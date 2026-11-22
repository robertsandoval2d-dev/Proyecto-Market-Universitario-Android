package com.market.univpe.feature.home.domain.usecases

import com.market.univpe.feature.auth.domain.repositories.AuthRepository
import com.market.univpe.feature.home.data.mappers.toUserSummary
import com.market.univpe.feature.home.domain.models.UserSummary
import com.market.univpe.feature.user.domain.repositories.UserRepository
import javax.inject.Inject

class GetUserSummaryUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): UserSummary {
        val currentUserId = authRepository.getCurrentUserId() ?: throw Exception("No hay usuario autenticado")

        val result = userRepository.getUser(currentUserId)

        return result.fold(
            onSuccess = { user ->
                if (user == null) {
                    throw Exception("Usuario no encontrado")
                }

                user.toUserSummary()

            },
            onFailure = {
                throw Exception("No se pudo obtener el usuario")
            }
        )
    }
}
