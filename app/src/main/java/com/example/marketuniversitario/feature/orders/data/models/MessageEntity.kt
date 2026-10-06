package com.example.marketuniversitario.feature.orders.data.models

import com.example.marketuniversitario.feature.orders.domain.models.Message
import com.google.firebase.firestore.DocumentId

data class MessageEntity(
    @DocumentId val id: String = "",
    val orderId: String = "",
    val senderId: String = "",
    val text: String? = null,
    val type: String = "",
    val audioUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

fun Message.toEntity() = MessageEntity(
    id = this.id,
    orderId = this.orderId,
    senderId = this.senderId,
    text = this.text,
    type = this.type,
    audioUrl = this.audioUrl,
    createdAt = this.createdAt
)

fun MessageEntity.toDomain() = Message(
    id = this.id,
    orderId = this.orderId,
    senderId = this.senderId,
    text = this.text,
    type = this.type,
    audioUrl = this.audioUrl,
    createdAt = this.createdAt
)
