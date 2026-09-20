package com.example.marketuniversitario.feature.auth.domain.repositories

import com.example.marketuniversitario.feature.auth.domain.models.UserSession

interface AuthRepository {
    suspend fun loginWithEmail(email: String, password: String): Result<UserSession>
    suspend fun signInWithGoogleToken(idToken: String): Result<UserSession>
    fun isUserLoggedIn(): Boolean
    suspend fun register(email: String, password: String): Result<UserSession>
    suspend fun sendEmailVerification(): Result<Boolean>
    fun isEmailVerified(): Boolean
    suspend fun sendPasswordResetEmail(email: String): Result<Boolean>
    suspend fun reloadUser(): Result<Unit>
    fun logout()
}