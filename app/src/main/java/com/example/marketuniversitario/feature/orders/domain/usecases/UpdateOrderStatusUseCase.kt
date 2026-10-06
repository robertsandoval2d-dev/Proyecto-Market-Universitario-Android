package com.example.marketuniversitario.feature.orders.domain.usecases

import com.example.marketuniversitario.feature.orders.domain.model.OrderStatus
import com.example.marketuniversitario.feature.orders.domain.repositories.OrderRepository
import javax.inject.Inject

class UpdateOrderStatusUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(orderId: String, newStatus: OrderStatus): Result<Unit> {
        return orderRepository.updateOrderStatus(orderId, newStatus)
    }
}
