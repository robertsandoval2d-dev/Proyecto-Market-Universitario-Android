package com.example.marketuniversitario.feature.orders.domain.repositories

import com.example.marketuniversitario.feature.orders.domain.models.Order
import com.example.marketuniversitario.feature.orders.domain.models.OrderStatus
import com.example.marketuniversitario.feature.orders.domain.models.Message
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun getOrders(): Result<List<Order>>
    fun getOrder(orderId: String): Flow<Order?>
    fun getMessagesForOrder(orderId: String): Flow<List<Message>>

    suspend fun createOrder(order: Order): Result<Order>

    fun getOrdersByBuyer(buyerId: String): Flow<List<Order>>

    fun getOrdersBySeller(sellerId: String): Flow<List<Order>>

    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit>

    suspend fun sendTextMessage(message: Message): Result<Unit>
    suspend fun sendVoiceMessage(message: Message, localAudioUri: android.net.Uri): Result<Unit>
}
