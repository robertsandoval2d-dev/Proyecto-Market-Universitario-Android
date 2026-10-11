package com.example.marketuniversitario.feature.orders.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.marketuniversitario.feature.orders.domain.models.Message

@Entity(tableName = "messages")
data class MessageRoomEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val senderId: String,
    val text: String?,
    val type: String,
    val audioUrl: String?,
    val createdAt: Long,
    val isSynced: Boolean = true
)

fun Message.toRoomEntity(isSynced: Boolean = true) = MessageRoomEntity(
    id = this.id,
    orderId = this.orderId,
    senderId = this.senderId,
    text = this.text,
    type = this.type,
    audioUrl = this.audioUrl,
    createdAt = this.createdAt,
    isSynced = isSynced
)

fun MessageRoomEntity.toDomain() = Message(
    id = this.id,
    orderId = this.orderId,
    senderId = this.senderId,
    text = this.text,
    type = this.type,
    audioUrl = this.audioUrl,
    createdAt = this.createdAt
)
