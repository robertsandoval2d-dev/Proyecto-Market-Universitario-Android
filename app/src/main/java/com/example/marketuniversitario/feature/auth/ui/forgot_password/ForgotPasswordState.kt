package com.example.marketuniversitario.feature.auth.ui.forgot_password

sealed interface ForgotPasswordStatus {
    object Idle : ForgotPasswordStatus
    object Loading : ForgotPasswordStatus
    object Success : ForgotPasswordStatus
    data class Error(val message: String) : ForgotPasswordStatus
}

data class ForgotPasswordState(
    val email: String = "",
    val status: ForgotPasswordStatus = ForgotPasswordStatus.Idle
)

sealed interface ForgotPasswordEvent {
    data class EmailChanged(val email: String) : ForgotPasswordEvent
    object Submit : ForgotPasswordEvent
    object DismissDialog : ForgotPasswordEvent
}