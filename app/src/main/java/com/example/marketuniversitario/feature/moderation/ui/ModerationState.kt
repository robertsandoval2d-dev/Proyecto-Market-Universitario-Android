package com.example.marketuniversitario.feature.moderation.ui

import com.example.marketuniversitario.feature.user.domain.model.AccountStatus
import com.example.marketuniversitario.feature.user.domain.model.User

enum class ModerationFilterTab(val displayName: String) {
    ALL("Todos"),
    ACTIVE("Activas"),
    SUSPENDED("Suspendidas"),
    BANNED("Baneadas")
}

data class ModerationState(
    val searchQuery: String = "",
    val selectedFilter: ModerationFilterTab = ModerationFilterTab.ALL,
    val users: List<User> = emptyList(),
    val filteredUsers: List<User> = emptyList(),
    val selectedUserForSanction: User? = null,
    val sanctionStatusToApply: AccountStatus = AccountStatus.SUSPENDED,
    val suspensionDurationDays: Int = 7, // 1, 3, 7, 30 días
    val sanctionReason: String = "",
    val isLoading: Boolean = false,
    val isExecutingAction: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

sealed interface ModerationEvent {
    data class SearchQueryChanged(val query: String) : ModerationEvent
    data class FilterTabChanged(val filter: ModerationFilterTab) : ModerationEvent
    data class OpenSanctionDialog(val user: User) : ModerationEvent
    object CloseSanctionDialog : ModerationEvent
    data class SanctionStatusChanged(val status: AccountStatus) : ModerationEvent
    data class SuspensionDurationChanged(val days: Int) : ModerationEvent
    data class SanctionReasonChanged(val reason: String) : ModerationEvent
    object ApplySanction : ModerationEvent
    data class RestoreAccount(val userId: String) : ModerationEvent
    object ClearMessages : ModerationEvent
}
