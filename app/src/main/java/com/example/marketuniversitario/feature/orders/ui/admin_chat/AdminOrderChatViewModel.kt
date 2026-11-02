package com.example.marketuniversitario.feature.orders.ui.admin_chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.orders.domain.usecases.GetAdminOrderMessagesUseCase
import com.example.marketuniversitario.feature.orders.domain.usecases.GetAdminOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminOrderChatViewModel @Inject constructor(
    private val getAdminOrderMessagesUseCase: GetAdminOrderMessagesUseCase,
    private val getAdminOrderUseCase: GetAdminOrderUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AdminOrderChatState())
    val state = _state.asStateFlow()

    fun onEvent(event: AdminOrderChatEvent) {
        when (event) {
            is AdminOrderChatEvent.InitOrder -> {
                _state.update { it.copy(orderId = event.orderId) }
                loadOrderAndMessages(event.orderId)
            }
            is AdminOrderChatEvent.PlayAudio -> {
                _state.update { it.copy(playingAudioUrl = event.url) }
            }
            is AdminOrderChatEvent.StopAudio -> {
                _state.update { it.copy(playingAudioUrl = null) }
            }
        }
    }

    private fun loadOrderAndMessages(orderId: String) {
        if (orderId.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val order = getAdminOrderUseCase(orderId)
            _state.update { it.copy(order = order) }

            getAdminOrderMessagesUseCase(orderId)
                .catch { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al cargar mensajes") }
                }
                .collect { messages ->
                    _state.update {
                        it.copy(
                            messages = messages,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }
}
