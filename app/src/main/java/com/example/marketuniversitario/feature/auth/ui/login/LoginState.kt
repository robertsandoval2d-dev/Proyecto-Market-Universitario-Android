package com.example.marketuniversitario.feature.auth.ui.login

sealed interface LoginStatus {
    object Idle : LoginStatus
    object Loading : LoginStatus
    object Success : LoginStatus
    data class Error(val message: String) : LoginStatus
}

data class LoginState(
    val email: String = "",
    val password: String = "",
    val status: LoginStatus = LoginStatus.Idle
)

sealed interface LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent
    data class PasswordChanged(val password: String) : LoginEvent
    object Login : LoginEvent
    object DismissDialog : LoginEvent
}