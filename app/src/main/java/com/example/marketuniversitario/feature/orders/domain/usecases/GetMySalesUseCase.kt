package com.example.marketuniversitario.feature.orders.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import com.example.marketuniversitario.feature.orders.domain.model.Order
import com.example.marketuniversitario.feature.orders.domain.repositories.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class GetMySalesUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<List<Order>> {
        val currentUserId = authRepository.getCurrentUserId()
        if (currentUserId.isNullOrEmpty()) {
            return emptyFlow()
        }
        return orderRepository.getOrdersBySeller(currentUserId)
    }
}
