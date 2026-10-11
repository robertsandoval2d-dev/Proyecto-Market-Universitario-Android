package com.example.marketuniversitario.feature.orders.data.workers

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.marketuniversitario.feature.orders.data.datasources.MessageLocalDataSource
import com.example.marketuniversitario.feature.orders.data.datasources.MessageRemoteDataSource
import com.example.marketuniversitario.feature.orders.data.models.MessageEntity
import com.example.marketuniversitario.feature.orders.data.models.toDomain
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import androidx.core.net.toUri

@HiltWorker
class SyncMessageWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val localDataSource: MessageLocalDataSource,
    private val remoteDataSource: MessageRemoteDataSource
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "SyncMessageWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val unsyncedMessages = localDataSource.getUnsyncedMessages()
            if (unsyncedMessages.isEmpty()) return Result.success()

            for (roomEntity in unsyncedMessages) {
                Log.d(TAG, "Syncing message: ${roomEntity.id}")
                val entity = MessageEntity(
                    id = roomEntity.id,
                    orderId = roomEntity.orderId,
                    senderId = roomEntity.senderId,
                    text = roomEntity.text,
                    type = roomEntity.type,
                    audioUrl = roomEntity.audioUrl,
                    createdAt = roomEntity.createdAt
                )

                if (roomEntity.type == "AUDIO" && !roomEntity.audioUrl.isNullOrBlank() && roomEntity.audioUrl.startsWith("file://")) {
                    val uri = roomEntity.audioUrl.toUri()
                    remoteDataSource.sendVoiceMessage(entity, uri)
                } else {
                    remoteDataSource.sendTextMessage(entity)
                }

                localDataSource.markAsSynced(roomEntity.id)
            }
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync messages", e)
            Result.retry()
        }
    }
}
