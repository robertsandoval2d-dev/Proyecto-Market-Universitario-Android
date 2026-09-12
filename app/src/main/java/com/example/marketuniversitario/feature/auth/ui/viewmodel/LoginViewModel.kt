package com.example.marketuniversitario.feature.auth.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.auth.domain.usecases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state

    fun login(email: String, pass: String) {
        _state.value = LoginState.Loading

        viewModelScope.launch {
            val result = loginUseCase.invoke(email,pass)
            result.onSuccess {
                _state.value = LoginState.Success
            }.onFailure { error ->
                _state.value = LoginState.Error(error.localizedMessage ?: "Error al iniciar sesión")
            }
        }
    }
}