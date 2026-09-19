package com.example.marketuniversitario.feature.auth.domain.usecases

import com.example.marketuniversitario.feature.auth.data.repositories.AuthRepositoryImpl
import com.example.marketuniversitario.feature.auth.domain.entities.UserSession
import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import javax.inject.Inject
import kotlin.math.log

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<UserSession> {

        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("El correo y la contraseña no pueden estar vacíos"))
        }

        if (!email.contains("@unmsm.edu.pe")) {
            return Result.failure(Exception("Formato de correo inválido"))
        }

        val session = authRepository.loginWithEmail(email, password)
            .getOrElse { return Result.failure(it) }

        if (!session.isEmailVerified) {
            authRepository.logout()
            return Result.failure(Exception("Debes verificar tu correo institucional antes de ingresar."))
        }

        return Result.success(session)
    }
}