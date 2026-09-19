package com.example.marketuniversitario.feature.auth.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.auth.data.datasources.GoogleAuthDataSource
import com.example.marketuniversitario.feature.auth.domain.exceptions.UserCancelledException
import com.example.marketuniversitario.feature.auth.domain.usecases.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val googleAuthDataSource: GoogleAuthDataSource,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(WelcomeState())
    val state = _state.asStateFlow()

    fun onGoogleSignInClick(context: Context) {
        viewModelScope.launch {
            _state.update { it.copy(status = WelcomeStatus.Loading) }

            googleAuthDataSource.getGoogleIdToken(context)
                .onSuccess { idToken ->
                    signInWithGoogleUseCase(idToken)
                        .onSuccess { _ ->
                            _state.update { it.copy(status = WelcomeStatus.Success) }
                        }
                        .onFailure { e ->
                            _state.update { it.copy(status = WelcomeStatus.Error(e.message ?: "Error al iniciar sesión")) }
                        }
                }
                .onFailure { e ->
                    if (e is UserCancelledException){
                        _state.update { it.copy(status = WelcomeStatus.Idle) }
                    } else {
                        _state.update {
                            it.copy(status = WelcomeStatus.Error(e.message ?: "Error al obtener credenciales"))
                        }
                    }
                }

        }
    }

    fun onDialogDismiss() {
        _state.update { it.copy(status = WelcomeStatus.Idle) }
    }
}