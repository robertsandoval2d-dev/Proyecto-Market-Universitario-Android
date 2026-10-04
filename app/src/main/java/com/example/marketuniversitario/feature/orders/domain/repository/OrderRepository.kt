package com.example.marketuniversitario.feature.orders.domain.repository

import com.example.marketuniversitario.feature.orders.domain.model.Message
import com.example.marketuniversitario.feature.orders.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun getOrders(): Result<List<Order>>
    fun getMessagesForOrder(orderId: String): Flow<List<Message>>
}