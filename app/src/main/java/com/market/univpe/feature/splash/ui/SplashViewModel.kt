package com.market.univpe.feature.splash.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.univpe.feature.auth.domain.repositories.AuthRepository
import com.market.univpe.feature.auth.domain.usecases.LogoutUseCase
import com.market.univpe.feature.user.domain.usecases.AccountStatusResult
import com.market.univpe.feature.user.domain.usecases.CheckProfileCompletionUseCase
import com.market.univpe.feature.user.domain.usecases.CheckUserIsAdminUseCase
import com.market.univpe.feature.user.domain.usecases.ValidateUserAccountStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor (
    private val authRepository: AuthRepository,
    private val logoutUseCase: LogoutUseCase,
    private val checkProfileCompletionUseCase: CheckProfileCompletionUseCase,
    private val checkUserIsAdminUseCase: CheckUserIsAdminUseCase,
    private val validateUserAccountStatusUseCase: ValidateUserAccountStatusUseCase
) : ViewModel() {
    private val _destination = MutableStateFlow<String?>(null)
    val destination = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            delay(2000)
            _destination.value = resolveStartDestination()
        }
    }

    private suspend fun resolveStartDestination(): String {
        if (!authRepository.isUserLoggedIn()) return "welcome"

        authRepository.reloadUser()

        if (!authRepository.isEmailVerified()) return "login"

        val userId = authRepository.getCurrentUserId().orEmpty()
        val accountStatusResult = validateUserAccountStatusUseCase(userId)
        if (accountStatusResult is AccountStatusResult.Blocked) {
            logoutUseCase()
            return "welcome"
        }

        val isProfileComplete = checkProfileCompletionUseCase().getOrDefault(false)
        if (!isProfileComplete) return "profile_completion"

        val isAdmin = checkUserIsAdminUseCase().getOrDefault(false)
        return if (isAdmin) "role_selection" else "inicio"
    }
}
