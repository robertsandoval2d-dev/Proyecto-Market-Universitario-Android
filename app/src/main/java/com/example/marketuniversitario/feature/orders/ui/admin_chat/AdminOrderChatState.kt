package com.example.marketuniversitario.feature.orders.ui.admin_chat

import com.example.marketuniversitario.feature.orders.domain.models.Message
import com.example.marketuniversitario.feature.orders.domain.models.Order

data class AdminOrderChatState(
    val orderId: String = "",
    val order: Order? = null,
    val messages: List<Message> = emptyList(),
    val playingAudioUrl: String? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

sealed interface AdminOrderChatEvent {
    data class InitOrder(val orderId: String) : AdminOrderChatEvent
    data class PlayAudio(val url: String) : AdminOrderChatEvent
    object StopAudio : AdminOrderChatEvent
}
