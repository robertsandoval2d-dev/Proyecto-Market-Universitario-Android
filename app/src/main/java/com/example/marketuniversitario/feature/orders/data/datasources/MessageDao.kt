package com.example.marketuniversitario.feature.orders.data.datasources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.marketuniversitario.feature.orders.data.models.MessageRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE orderId = :orderId ORDER BY createdAt ASC")
    fun getMessagesForOrder(orderId: String): Flow<List<MessageRoomEntity>>

    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: String): MessageRoomEntity?

    @Query("SELECT * FROM messages WHERE isSynced = 0")
    suspend fun getUnsyncedMessages(): List<MessageRoomEntity>

    @Query("UPDATE messages SET isSynced = 1 WHERE id = :messageId")
    suspend fun markAsSynced(messageId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageRoomEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageRoomEntity)
}
