package com.example.marketuniversitario.feature.orders.data.repository

import com.example.marketuniversitario.feature.orders.domain.model.Message
import com.example.marketuniversitario.feature.orders.domain.model.Order
import com.example.marketuniversitario.feature.orders.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor() : OrderRepository {

    override suspend fun getOrders(): Result<List<Order>> {
        // TODO: Implementar consulta a Firestore (Colección orders)
        return Result.success(emptyList())
    }

    override fun getMessagesForOrder(orderId: String): Flow<List<Message>> {
        // TODO: Implementar consulta a Firestore (Subcolección messages)
        return emptyFlow()
    }
}