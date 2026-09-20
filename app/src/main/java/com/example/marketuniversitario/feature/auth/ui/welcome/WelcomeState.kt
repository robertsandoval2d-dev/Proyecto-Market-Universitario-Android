package com.example.marketuniversitario.feature.auth.ui.welcome

sealed interface WelcomeStatus {
    object Idle: WelcomeStatus
    object Loading: WelcomeStatus
    object Success: WelcomeStatus
    data class Error(val message: String): WelcomeStatus
}

data class WelcomeState(
    val status: WelcomeStatus = WelcomeStatus.Idle
)
