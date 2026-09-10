package com.example.marketuniversitario.feature.auth.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.auth.data.AuthRepositoryImpl
import com.example.marketuniversitario.feature.auth.domain.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {
    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _state.value = LoginState.Error("Completa todos los campos")
            return
        }

        viewModelScope.launch {
            _state.value = LoginState.Loading
            val result = repository.loginWithEmail(email, pass)
            result.onSuccess {
                _state.value = LoginState.Success
            }.onFailure { error ->
                _state.value = LoginState.Error(error.localizedMessage ?: "Error al iniciar sesión")
            }
        }
    }
}