package com.example.marketuniversitario.feature.orders.domain.models

enum class MessageType { TEXT, AUDIO }
data class Message(
    val id: String = "",
    val orderId: String = "",
    val senderId: String = "",
    val text: String? = null,
    val type: String = MessageType.TEXT.name,
    val audioUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)