package com.example.marketuniversitario.feature.auth.domain.repositories

interface AuthRepository {
    suspend fun loginWithEmail(email: String, password: String): Result<Unit>
    fun isUserLoggedIn(): Boolean
    suspend fun register(email: String, password: String): Result<Boolean>
    suspend fun sendEmailVerification(): Result<Boolean>
    fun isEmailVerified(): Boolean
    fun logout()
}