package com.example.marketuniversitario.feature.profile.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PreferencesState())
    val state = _state.asStateFlow()

    init {
        loadUser()
    }

    fun onEvent(event: PreferencesEvent) {
        when (event) {
            is PreferencesEvent.TogglePreference -> togglePreference(event.pref)
            is PreferencesEvent.SelectIntent -> _state.update { it.copy(primaryIntent = event.intent) }
            is PreferencesEvent.SavePreferences -> savePreferences()
            is PreferencesEvent.ResetStatus -> _state.update { it.copy(isSuccess = false, error = null) }
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserId() ?: return@launch
            userRepository.getUser(uid).onSuccess { user ->
                if (user != null) {
                    _state.update {
                        it.copy(
                            user = user,
                            selectedPreferences = user.preferences,
                            primaryIntent = user.primaryIntent ?: ""
                        )
                    }
                }
            }
        }
    }

    private fun togglePreference(pref: String) {
        val current = _state.value.selectedPreferences.toMutableList()
        if (current.contains(pref)) {
            current.remove(pref)
            _state.update { it.copy(selectedPreferences = current, error = null) }
        } else {
            if (current.size < 3) {
                current.add(pref)
                _state.update { it.copy(selectedPreferences = current, error = null) }
            } else {
                _state.update { it.copy(error = "Puedes seleccionar un máximo de 3 preferencias") }
            }
        }
    }

    private fun savePreferences() {
        val currentState = _state.value
        val user = currentState.user ?: return

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val updatedUser = user.copy(
                preferences = currentState.selectedPreferences,
                primaryIntent = currentState.primaryIntent
            )

            userRepository.updateUserProfile(updatedUser).onSuccess {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message ?: "Error al guardar preferencias") }
            }
        }
    }
}