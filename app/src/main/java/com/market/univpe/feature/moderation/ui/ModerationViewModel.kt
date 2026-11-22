package com.market.univpe.feature.moderation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.univpe.feature.user.domain.model.AccountStatus
import com.market.univpe.feature.user.domain.model.User
import com.market.univpe.feature.user.domain.usecases.ApplyUserSanctionUseCase
import com.market.univpe.feature.user.domain.usecases.GetAllUsersUseCase
import com.market.univpe.feature.user.domain.usecases.RestoreUserAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModerationViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val applyUserSanctionUseCase: ApplyUserSanctionUseCase,
    private val restoreUserAccountUseCase: RestoreUserAccountUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ModerationState())
    val state = _state.asStateFlow()

    init {
        loadUsers()
    }

    fun onEvent(event: ModerationEvent) {
        when (event) {
            is ModerationEvent.SearchQueryChanged -> {
                _state.update {
                    val updated = it.copy(searchQuery = event.query)
                    updated.copy(filteredUsers = filterUsers(updated.users, event.query, updated.selectedFilter))
                }
            }
            is ModerationEvent.FilterTabChanged -> {
                _state.update {
                    val updated = it.copy(selectedFilter = event.filter)
                    updated.copy(filteredUsers = filterUsers(updated.users, updated.searchQuery, event.filter))
                }
            }
            is ModerationEvent.OpenSanctionDialog -> {
                _state.update {
                    it.copy(
                        selectedUserForSanction = event.user,
                        sanctionStatusToApply = AccountStatus.SUSPENDED,
                        suspensionDurationDays = 7,
                        sanctionReason = ""
                    )
                }
            }
            is ModerationEvent.CloseSanctionDialog -> {
                _state.update { it.copy(selectedUserForSanction = null) }
            }
            is ModerationEvent.SanctionStatusChanged -> {
                _state.update { it.copy(sanctionStatusToApply = event.status) }
            }
            is ModerationEvent.SuspensionDurationChanged -> {
                _state.update { it.copy(suspensionDurationDays = event.days) }
            }
            is ModerationEvent.SanctionReasonChanged -> {
                _state.update { it.copy(sanctionReason = event.reason) }
            }
            is ModerationEvent.ApplySanction -> {
                applySanction()
            }
            is ModerationEvent.RestoreAccount -> {
                restoreAccount(event.userId)
            }
            is ModerationEvent.ClearMessages -> {
                _state.update { it.copy(errorMessage = null, successMessage = null) }
            }
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getAllUsersUseCase()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al cargar usuarios") }
                }
                .collect { users ->
                    _state.update {
                        it.copy(
                            users = users,
                            filteredUsers = filterUsers(users, it.searchQuery, it.selectedFilter),
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun filterUsers(
        users: List<User>,
        query: String,
        filter: ModerationFilterTab
    ): List<User> {
        return users.filter { user ->
            val matchesFilter = when (filter) {
                ModerationFilterTab.ALL -> true
                ModerationFilterTab.ACTIVE -> user.accountStatus == AccountStatus.ACTIVE
                ModerationFilterTab.SUSPENDED -> user.accountStatus == AccountStatus.SUSPENDED
                ModerationFilterTab.BANNED -> user.accountStatus == AccountStatus.BANNED
            }
            val matchesQuery = query.isBlank() ||
                    user.name.contains(query, ignoreCase = true) ||
                    user.email.contains(query, ignoreCase = true) ||
                    user.phone.contains(query, ignoreCase = true)

            matchesFilter && matchesQuery
        }
    }

    private fun applySanction() {
        val currentState = _state.value
        val user = currentState.selectedUserForSanction ?: return
        val reason = currentState.sanctionReason.trim()

        if (reason.isBlank()) {
            _state.update { it.copy(errorMessage = "Debes ingresar un motivo para la sanción") }
            return
        }

        val status = currentState.sanctionStatusToApply
        val suspendedUntil = if (status == AccountStatus.SUSPENDED) {
            System.currentTimeMillis() + (currentState.suspensionDurationDays * 24 * 60 * 60 * 1000L)
        } else {
            null
        }

        viewModelScope.launch {
            _state.update { it.copy(isExecutingAction = true, errorMessage = null) }

            applyUserSanctionUseCase(
                userId = user.id,
                status = status,
                suspendedUntil = suspendedUntil,
                reason = reason
            )
                .onSuccess {
                    val statusText = if (status == AccountStatus.BANNED) "baneado permanentemente" else "suspendido por ${currentState.suspensionDurationDays} días"
                    _state.update {
                        it.copy(
                            isExecutingAction = false,
                            selectedUserForSanction = null,
                            sanctionReason = "",
                            successMessage = "El usuario ${user.name} fue $statusText."
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isExecutingAction = false,
                            errorMessage = error.message ?: "Error al aplicar sanción"
                        )
                    }
                }
        }
    }

    private fun restoreAccount(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isExecutingAction = true, errorMessage = null) }

            restoreUserAccountUseCase(userId)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isExecutingAction = false,
                            successMessage = "Cuenta de usuario restaurada a estado activo."
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isExecutingAction = false,
                            errorMessage = error.message ?: "Error al restaurar cuenta"
                        )
                    }
                }
        }
    }
}
