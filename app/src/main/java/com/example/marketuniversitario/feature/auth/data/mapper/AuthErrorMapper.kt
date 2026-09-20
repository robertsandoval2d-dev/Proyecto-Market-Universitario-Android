package com.example.marketuniversitario.feature.auth.data.mapper

import com.example.marketuniversitario.feature.auth.domain.exceptions.AuthException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

object AuthErrorMapper {
    fun map(e: Exception): AuthException {
        return when (e) {
            is FirebaseAuthUserCollisionException -> AuthException.EmailAlreadyInUse
            is FirebaseAuthWeakPasswordException -> AuthException.WeakPassword
            is FirebaseAuthInvalidCredentialsException,
            is FirebaseAuthInvalidUserException -> AuthException.InvalidCredentials
            is FirebaseNetworkException -> AuthException.Network
            is AuthException -> e
            else -> AuthException.Unknown(e)
        }
    }
}