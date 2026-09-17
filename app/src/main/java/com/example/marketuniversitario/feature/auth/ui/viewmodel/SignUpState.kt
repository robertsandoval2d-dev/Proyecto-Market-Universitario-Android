package com.example.marketuniversitario.feature.auth.ui.viewmodel

sealed interface SignUpStatus {
    object Idle: SignUpStatus
    object Loading: SignUpStatus
    object Success: SignUpStatus
    data class Error(val message: String): SignUpStatus
}

data class SignUpState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val status: SignUpStatus = SignUpStatus.Idle
)

sealed interface SignUpEvent {
    data class EmailChanged(val email: String) : SignUpEvent
    data class PasswordChanged(val password: String) : SignUpEvent
    data class ConfirmPasswordChanged(val confirmPassword: String) : SignUpEvent
    object SignUp : SignUpEvent
    object DismissDialog : SignUpEvent
}