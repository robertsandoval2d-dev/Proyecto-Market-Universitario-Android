package com.example.marketuniversitario.feature.auth.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.auth.domain.usecases.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.EmailChanged -> _state.update { it.copy(email = event.email) }
            is SignUpEvent.PasswordChanged -> _state.update { it.copy(password = event.password) }
            is SignUpEvent.ConfirmPasswordChanged -> _state.update { it.copy(confirmPassword = event.confirmPassword) }
            is SignUpEvent.SignUp -> register()
            is SignUpEvent.DismissDialog -> _state.update { it.copy(status = SignUpStatus.Idle) }
        }
    }

    private fun register() {
        val currentState = _state.value
        val email = currentState.email.trim()
        val password = currentState.password
        val confirmPassword = currentState.confirmPassword

        if (email.isBlank() || password.isBlank()) {
            _state.update { it.copy(status = SignUpStatus.Error("El correo y la contraseña no pueden estar vacíos")) }
            return
        }

        if (password != confirmPassword) {
            _state.update { it.copy(status = SignUpStatus.Error("Las contraseñas no coinciden")) }
            return
        }

        _state.update { it.copy(status = SignUpStatus.Loading) }

        viewModelScope.launch {
            signUpUseCase.invoke(email, password)
                .onSuccess {
                    _state.update { it.copy(status = SignUpStatus.Success) }
                }
                .onFailure { error ->
                    _state.update { it.copy(status = SignUpStatus.Error(error.localizedMessage ?: "Error al registrar")) }
                }
        }
    }
}