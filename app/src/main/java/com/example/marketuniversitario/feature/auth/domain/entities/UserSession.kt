package com.example.marketuniversitario.feature.auth.domain.entities

data class UserSession(
    val uid: String,
    val email: String,
    val isEmailVerified: Boolean = false,
    val displayName: String? = null
)
