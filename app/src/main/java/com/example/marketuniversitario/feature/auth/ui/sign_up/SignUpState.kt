package com.example.marketuniversitario.feature.auth.ui.sign_up

import android.content.Context

sealed interface SignUpStatus {
    object Idle: SignUpStatus
    object Loading: SignUpStatus
    data class Success(val isEmailVerified: Boolean) : SignUpStatus
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

    data class GoogleSignIn(val context: Context) : SignUpEvent
    object DismissDialog : SignUpEvent
}