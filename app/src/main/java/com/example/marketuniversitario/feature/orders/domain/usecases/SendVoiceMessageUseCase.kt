package com.example.marketuniversitario.feature.orders.domain.usecases

import android.net.Uri
import com.example.marketuniversitario.feature.orders.domain.models.Message
import com.example.marketuniversitario.feature.orders.domain.models.MessageType
import com.example.marketuniversitario.feature.orders.domain.repositories.OrderRepository
import java.util.UUID
import javax.inject.Inject

class SendVoiceMessageUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(orderId: String, senderId: String, audioUri: Uri): Result<Unit> {
        return try {
            val message = Message(
                id = UUID.randomUUID().toString(),
                orderId = orderId,
                senderId = senderId,
                text = null,
                type = MessageType.AUDIO.name,
                audioUrl = null,
                createdAt = System.currentTimeMillis()
            )
            orderRepository.sendVoiceMessage(message, audioUri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
