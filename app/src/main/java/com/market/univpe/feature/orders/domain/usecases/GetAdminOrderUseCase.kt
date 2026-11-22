package com.market.univpe.feature.orders.domain.usecases

import com.market.univpe.feature.orders.domain.models.Order
import com.market.univpe.feature.orders.domain.repositories.OrderRepository
import javax.inject.Inject

class GetAdminOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(orderId: String): Order? {
        return orderRepository.getOrderRemote(orderId).getOrNull()
    }
}
