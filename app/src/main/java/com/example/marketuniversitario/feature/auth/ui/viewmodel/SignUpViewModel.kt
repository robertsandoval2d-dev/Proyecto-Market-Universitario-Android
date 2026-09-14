package com.example.marketuniversitario.feature.auth.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.auth.domain.usecases.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val state: StateFlow<SignUpState> = _state

    fun register(email: String, password: String, confirmPassword: String) {
        _state.value = SignUpState.Loading

        if (email.isBlank() || password.isBlank()) {
            _state.value = SignUpState.Error("El correo y la contraseña no pueden estar vacíos")
            return
        }

        if(password != confirmPassword){
            _state.value = SignUpState.Error("Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            val result = signUpUseCase.invoke(email, password)
            result.onSuccess {
                _state.value = SignUpState.Success
            }.onFailure { error ->
                _state.value = SignUpState.Error(error.localizedMessage ?: "Error al registrar")
            }
        }
    }
}