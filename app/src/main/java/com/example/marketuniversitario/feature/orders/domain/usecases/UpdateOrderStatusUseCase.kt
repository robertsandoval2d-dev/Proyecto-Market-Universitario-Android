package com.example.marketuniversitario.feature.orders.domain.usecases

import com.example.marketuniversitario.feature.business.domain.repositories.ProductRepository
import com.example.marketuniversitario.feature.orders.domain.models.OrderStatus
import com.example.marketuniversitario.feature.orders.domain.repositories.OrderRepository
import javax.inject.Inject

class UpdateOrderStatusUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(orderId: String, newStatus: OrderStatus): Result<Unit> {
        val result = orderRepository.updateOrderStatus(orderId, newStatus)
        if (result.isSuccess) {
            val order = orderRepository.getOrderById(orderId)
            if (order != null && order.productId.isNotBlank()) {
                when (newStatus) {
                    OrderStatus.COMPLETED -> {
                        // Incrementar ventas y confirmar (reducir) stock reservado
                        productRepository.incrementSalesCount(order.productId)
                        productRepository.confirmReservedStock(order.productId, order.quantity)
                    }
                    OrderStatus.REJECTED -> {
                        // Liberar stock reservado (restarlo de reservedStock y sumarlo a stock)
                        productRepository.releaseReservedStock(order.productId, order.quantity)
                    }
                    else -> {}
                }
            }
        }
        return result
    }
}
