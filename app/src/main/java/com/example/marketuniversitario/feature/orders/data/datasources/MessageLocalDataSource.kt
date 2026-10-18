package com.example.marketuniversitario.feature.orders.data.datasources

import com.example.marketuniversitario.feature.orders.data.models.MessageRoomEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MessageLocalDataSource @Inject constructor(
    private val messageDao: MessageDao
) {
    fun getMessagesForOrder(orderId: String): Flow<List<MessageRoomEntity>> {
        return messageDao.getMessagesForOrder(orderId)
    }

    suspend fun getMessageById(messageId: String): MessageRoomEntity? {
        return messageDao.getMessageById(messageId)
    }

    suspend fun getUnsyncedMessages(): List<MessageRoomEntity> {
        return messageDao.getUnsyncedMessages()
    }

    suspend fun markAsSynced(messageId: String) {
        messageDao.markAsSynced(messageId)
    }

    suspend fun insertMessages(messages: List<MessageRoomEntity>) {
        messageDao.insertMessages(messages)
    }

    suspend fun insertMessage(message: MessageRoomEntity) {
        messageDao.insertMessage(message)
    }
}
