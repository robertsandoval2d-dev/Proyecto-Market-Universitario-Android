package com.example.marketuniversitario.feature.auth.ui.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.auth.domain.usecases.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state.asStateFlow()

    fun onEvent(event: ForgotPasswordEvent) {
        when (event) {
            is ForgotPasswordEvent.EmailChanged -> _state.update { it.copy(email = event.email) }
            is ForgotPasswordEvent.Submit -> sendResetEmail()
            is ForgotPasswordEvent.DismissDialog -> _state.update { it.copy(status = ForgotPasswordStatus.Idle) }
        }
    }

    private fun sendResetEmail() {
        val email = _state.value.email.trim()

        if (email.isBlank()) {
            _state.update { it.copy(status = ForgotPasswordStatus.Error("El correo no puede estar vacío")) }
            return
        }

        _state.update { it.copy(status = ForgotPasswordStatus.Loading) }

        viewModelScope.launch {
            resetPasswordUseCase.invoke(email)
                .onSuccess {
                    _state.update { it.copy(status = ForgotPasswordStatus.Success) }
                }
                .onFailure { error ->
                    _state.update { it.copy(status = ForgotPasswordStatus.Error(error.localizedMessage ?: "Error al enviar el correo")) }
                }
        }
    }
}