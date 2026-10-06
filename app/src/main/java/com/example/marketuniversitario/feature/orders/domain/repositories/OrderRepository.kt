package com.example.marketuniversitario.feature.orders.domain.repositories

import com.example.marketuniversitario.feature.orders.domain.model.Order
import com.example.marketuniversitario.feature.orders.domain.model.OrderStatus
import com.example.marketuniversitario.feature.orders.domain.models.Message
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun getOrders(): Result<List<Order>>
    fun getMessagesForOrder(orderId: String): Flow<List<Message>>

    suspend fun createOrder(order: Order): Result<Order>

    fun getOrdersByBuyer(buyerId: String): Flow<List<Order>>

    fun getOrdersBySeller(sellerId: String): Flow<List<Order>>

    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit>
}