package com.example.marketuniversitario.feature.splash.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor (
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _destination = MutableStateFlow<String?>(null)
    val destination = _destination.asStateFlow()

    init {
        authRepository.logout() //PRUEBAS
        viewModelScope.launch {
            _destination.value = resolveStartDestination()
        }
    }

    private suspend fun resolveStartDestination(): String {
        if (!authRepository.isUserLoggedIn()) return "welcome"

        authRepository.reloadUser()

        return if (authRepository.isEmailVerified()) "inicio" else "login"
    }
}