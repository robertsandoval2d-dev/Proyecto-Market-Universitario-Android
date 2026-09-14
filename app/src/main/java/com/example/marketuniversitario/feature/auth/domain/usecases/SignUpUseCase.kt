package com.example.marketuniversitario.feature.auth.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Boolean> {

        val allowedDomain = "@unmsm.edu.pe"

        if (!email.endsWith(allowedDomain)) {
            return Result.failure(Exception("Formato de correo inválido"))
        }

        if (password.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres."))
        }

        val registerResult = authRepository.register(email,password)

        if(registerResult.isFailure){
            return  registerResult
        }

        val verificationResult = authRepository.sendEmailVerification()

        return if(verificationResult.isSuccess){
            Result.success(true)
        } else {
            Result.failure(Exception("Cuenta creada, pero hubo un error enviando el correo de verificación."))
        }
    }
}