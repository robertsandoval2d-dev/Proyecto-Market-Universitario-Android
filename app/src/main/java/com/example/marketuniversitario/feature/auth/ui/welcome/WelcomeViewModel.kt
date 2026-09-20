package com.example.marketuniversitario.feature.auth.ui.welcome

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.auth.domain.exceptions.AuthException
import com.example.marketuniversitario.feature.auth.domain.usecases.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(WelcomeState())
    val state = _state.asStateFlow()

    fun onGoogleSignInClick(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(status = WelcomeStatus.Loading) }

            signInWithGoogleUseCase(idToken)
                .onSuccess { _ ->
                    _state.update { it.copy(status = WelcomeStatus.Success) }
                }
                .onFailure { e ->
                    _state.update { it.copy(status = WelcomeStatus.Error(e.message ?: "Error al iniciar sesión")) }
                }
        }
    }

    fun onGoogleSignInError(exception: Throwable) {
        val newStatus = if (exception is AuthException.UserCancelled) {
            WelcomeStatus.Idle
        } else {
            WelcomeStatus.Error(exception.message ?: "Error al obtener credenciales")
        }
        _state.update { it.copy(status = newStatus) }
    }

    fun onDialogDismiss() {
        _state.update { it.copy(status = WelcomeStatus.Idle) }
    }
}