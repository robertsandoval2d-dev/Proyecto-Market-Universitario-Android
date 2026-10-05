package com.example.marketuniversitario.feature.home.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import com.example.marketuniversitario.feature.home.data.toUserSummary
import com.example.marketuniversitario.feature.home.domain.models.UserSummary
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
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

                val summary = user.toUserSummary()

                if (summary.firstName.equals("Sebastian", ignoreCase = true)) {
                    summary.copy(firstName = "Chebas")
                } else {
                    summary
                }

            },
            onFailure = {
                throw Exception("No se pudo obtener el usuario")
            }
        )
    }
}
