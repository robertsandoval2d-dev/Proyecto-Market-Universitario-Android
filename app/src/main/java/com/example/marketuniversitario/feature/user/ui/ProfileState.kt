package com.example.marketuniversitario.feature.user.ui

import com.example.marketuniversitario.feature.user.domain.model.User

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface ProfileEvent {
    data object LoadProfile : ProfileEvent
    data object Logout : ProfileEvent
}