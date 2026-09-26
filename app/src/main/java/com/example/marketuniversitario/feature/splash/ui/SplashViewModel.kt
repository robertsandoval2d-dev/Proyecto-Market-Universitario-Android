package com.example.marketuniversitario.feature.splash.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.marketuniversitario.feature.user.domain.usecases.CheckProfileCompletionUseCase

@HiltViewModel
class SplashViewModel @Inject constructor (
    private val authRepository: AuthRepository,
    private val checkProfileCompletionUseCase: CheckProfileCompletionUseCase
) : ViewModel() {
    private val _destination = MutableStateFlow<String?>(null)
    val destination = _destination.asStateFlow()

    init {
        authRepository.logout() //PRUEBAS
        viewModelScope.launch {
            delay(2000)
            _destination.value = resolveStartDestination()
        }
    }

    private suspend fun resolveStartDestination(): String {
        if (!authRepository.isUserLoggedIn()) return "welcome"

        authRepository.reloadUser()

        if (!authRepository.isEmailVerified()) return "login"

        val isProfileComplete = checkProfileCompletionUseCase().getOrDefault(false)
        return if (isProfileComplete) "inicio" else "profile_completion"
    }
}