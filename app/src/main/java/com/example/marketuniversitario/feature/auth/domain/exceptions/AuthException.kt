package com.example.marketuniversitario.feature.auth.domain.exceptions

sealed class AuthException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause) {

    object UserCancelled : AuthException("Usuario cancela acción")
    //SignUp
    object NotUniversityAccount : AuthException("La cuenta debe ser institucional (@unmsm.edu.pe)")
    object EmailAlreadyInUse : AuthException("Este correo ya está registrado")
    object WeakPassword : AuthException("La contraseña debe tener al menos 6 caracteres.")
    //Login
    object InvalidCredentials : AuthException("El correo o la contraseña son incorrectos")
    object NotVerifiedEmail : AuthException("El correo no ha sido verificado")
    //Extra
    object Network : AuthException("Sin conexión a internet")
    object TooManyRequests : AuthException("Demasiados intentos, intenta más tarde")
    class Unknown(cause: Throwable?) : AuthException("Ocurrió un error inesperado", cause)
}