package com.example.marketuniversitario.feature.orders.domain.usecases

import com.example.marketuniversitario.feature.orders.domain.models.Message
import com.example.marketuniversitario.feature.orders.domain.repositories.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesForOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(orderId: String): Flow<List<Message>> {
        return orderRepository.getMessagesForOrder(orderId)
    }
}
