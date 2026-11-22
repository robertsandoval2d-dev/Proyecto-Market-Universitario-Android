package com.market.univpe.feature.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.univpe.feature.auth.domain.usecases.LoginUseCase
import com.market.univpe.feature.auth.domain.usecases.LogoutUseCase
import com.market.univpe.feature.user.domain.usecases.AccountStatusResult
import com.market.univpe.feature.user.domain.usecases.CheckProfileCompletionUseCase
import com.market.univpe.feature.user.domain.usecases.CheckUserIsAdminUseCase
import com.market.univpe.feature.user.domain.usecases.ValidateUserAccountStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val checkProfileCompletionUseCase: CheckProfileCompletionUseCase,
    private val checkUserIsAdminUseCase: CheckUserIsAdminUseCase,
    private val validateUserAccountStatusUseCase: ValidateUserAccountStatusUseCase
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
                .onSuccess { session ->
                    val accountStatusResult = validateUserAccountStatusUseCase(session.uid)
                    if (accountStatusResult is AccountStatusResult.Blocked) {
                        logoutUseCase()
                        _state.update { it.copy(status = LoginStatus.Error(accountStatusResult.message)) }
                        return@launch
                    }

                    val isComplete = checkProfileCompletionUseCase().getOrDefault(false)
                    val isAdmin = checkUserIsAdminUseCase().getOrDefault(false)
                    _state.update { it.copy(status = LoginStatus.Success(isComplete, isAdmin)) }
                }
                .onFailure { error ->
                    _state.update { it.copy(status = LoginStatus.Error(error.localizedMessage ?: "Error al iniciar sesión")) }
                }
        }
    }
}
