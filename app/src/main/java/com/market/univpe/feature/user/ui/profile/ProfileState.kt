package com.market.univpe.feature.user.ui.profile

import com.market.univpe.feature.user.domain.model.User

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface ProfileEvent {
    data object LoadProfile : ProfileEvent
    data object Logout : ProfileEvent
}