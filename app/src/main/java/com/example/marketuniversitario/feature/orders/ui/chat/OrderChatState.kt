package com.example.marketuniversitario.feature.orders.ui.chat

import android.net.Uri
import com.example.marketuniversitario.feature.orders.domain.models.Order
import com.example.marketuniversitario.feature.orders.domain.models.Message

data class OrderChatState(
    val order: Order = Order(),
    val messages: List<Message> = emptyList(),
    val currentUserId: String = "",
    val messageText: String = "",
    val isRecording: Boolean = false,
    val playingAudioUrl: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface OrderChatEvent {
    data class MessageTextChanged(val text: String) : OrderChatEvent
    object SendMessage : OrderChatEvent
    data class SendVoiceMessage(val audioUri: Uri) : OrderChatEvent
    object StartRecording : OrderChatEvent
    object StopRecording : OrderChatEvent
    data class PlayAudio(val url: String) : OrderChatEvent
    object StopAudio : OrderChatEvent
    object BackClicked : OrderChatEvent
}
