package com.example.marketuniversitario.feature.profile.ui.preferences

import com.example.marketuniversitario.feature.user.domain.model.User

data class PreferencesState(
    val user: User? = null,
    val selectedPreferences: List<String> = emptyList(),
    val primaryIntent: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

sealed interface PreferencesEvent {
    data class TogglePreference(val pref: String) : PreferencesEvent
    data class SelectIntent(val intent: String) : PreferencesEvent
    data object SavePreferences : PreferencesEvent
    data object ResetStatus : PreferencesEvent
}