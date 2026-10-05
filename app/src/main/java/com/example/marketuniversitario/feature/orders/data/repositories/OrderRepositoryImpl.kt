package com.example.marketuniversitario.feature.orders.data.repositories

import com.example.marketuniversitario.feature.orders.data.datasources.OrderRemoteDataSource
import com.example.marketuniversitario.feature.orders.data.models.toEntity
import com.example.marketuniversitario.feature.orders.domain.model.Order
import com.example.marketuniversitario.feature.orders.domain.model.OrderStatus
import com.example.marketuniversitario.feature.orders.domain.models.Message
import com.example.marketuniversitario.feature.orders.domain.repositories.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val remoteDataSource: OrderRemoteDataSource
) : OrderRepository {

    override suspend fun getOrders(): Result<List<Order>> {
        TODO("Not yet implemented")
    }

    override suspend fun createOrder(order: Order): Result<Order> {
        return try {
            val entity = order.toEntity()
            val generatedId = remoteDataSource.createOrder(entity)
            Result.success(order.copy(id = generatedId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getOrdersByBuyer(buyerId: String): Result<List<Order>> {
        // TODO: Implementar consulta a Firestore (Colección orders)
        return Result.success(emptyList())
    }

    override suspend fun getOrdersBySeller(buyerId: String): Result<List<Order>> {
        // TODO: Implementar consulta a Firestore (Colección orders)
        return Result.success(emptyList())
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit> {
        TODO("Not yet implemented")
    }



    override fun getMessagesForOrder(orderId: String): Flow<List<Message>> {
        // TODO: Implementar consulta a Firestore (Subcolección messages)
        return emptyFlow()
    }
}