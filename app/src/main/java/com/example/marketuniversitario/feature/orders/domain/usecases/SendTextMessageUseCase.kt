package com.example.marketuniversitario.feature.orders.domain.usecases

import com.example.marketuniversitario.feature.orders.domain.models.Message
import com.example.marketuniversitario.feature.orders.domain.repositories.OrderRepository
import java.util.UUID
import javax.inject.Inject

class SendTextMessageUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(orderId: String, senderId: String, text: String): Result<Unit> {
        val trimmedText = text.trim()
        if (trimmedText.isBlank()) {
            return Result.failure(IllegalArgumentException("El mensaje no puede estar vacío"))
        }

        val message = Message(
            id = UUID.randomUUID().toString(),
            orderId = orderId,
            senderId = senderId,
            text = trimmedText,
            createdAt = System.currentTimeMillis()
        )

        return orderRepository.sendTextMessage(message)
    }
}
