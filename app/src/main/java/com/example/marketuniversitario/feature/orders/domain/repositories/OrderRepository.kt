package com.example.marketuniversitario.feature.orders.domain.repositories

import com.example.marketuniversitario.feature.orders.domain.models.Message
import com.example.marketuniversitario.feature.orders.domain.models.Order
import com.example.marketuniversitario.feature.orders.domain.models.OrderStatus
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun getOrders(): Result<List<Order>>
    fun getMessagesForOrder(orderId: String): Flow<List<Message>>

    suspend fun createOrder(order: Order): Result<Order>

    suspend fun getOrdersByBuyer(buyerId: String): Result<List<Order>>

    suspend fun getOrdersBySeller(sellerId: String): Result<List<Order>>

    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit>
}