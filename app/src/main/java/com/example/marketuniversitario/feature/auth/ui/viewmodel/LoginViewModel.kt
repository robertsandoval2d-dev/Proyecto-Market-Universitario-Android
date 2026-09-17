package com.example.marketuniversitario.feature.auth.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.auth.domain.usecases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> _state.update { it.copy(email = event.email) }
            is LoginEvent.PasswordChanged -> _state.update { it.copy(password = event.password) }
            is LoginEvent.Login -> login()
            is LoginEvent.DismissDialog -> _state.update { it.copy(status = LoginStatus.Idle) }
        }
    }

    private fun login() {
        val currentState = _state.value
        val email = currentState.email.trim()
        val password = currentState.password

        if (email.isBlank() || password.isBlank()) {
            _state.update { it.copy(status = LoginStatus.Error("El correo y la contraseña no pueden estar vacíos")) }
            return
        }

        _state.update { it.copy(status = LoginStatus.Loading) }

        viewModelScope.launch {
            loginUseCase.invoke(email, password)
                .onSuccess {
                    _state.update { it.copy(status = LoginStatus.Success) }
                }
                .onFailure { error ->
                    _state.update { it.copy(status = LoginStatus.Error(error.localizedMessage ?: "Error al iniciar sesión")) }
                }
        }
    }
}