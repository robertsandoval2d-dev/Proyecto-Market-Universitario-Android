package com.example.marketuniversitario.feature.orders.data.repositories

import com.example.marketuniversitario.feature.orders.data.datasources.OrderRemoteDataSource
import com.example.marketuniversitario.feature.orders.data.models.toEntity
import com.example.marketuniversitario.feature.orders.data.models.toDomain
import com.example.marketuniversitario.feature.orders.domain.model.Order
import com.example.marketuniversitario.feature.orders.domain.model.OrderStatus
import com.example.marketuniversitario.feature.orders.domain.models.Message
import com.example.marketuniversitario.feature.orders.domain.repositories.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
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

    override fun getOrdersByBuyer(buyerId: String): Flow<List<Order>> {
        return remoteDataSource.getOrdersByBuyerFlow(buyerId).map { entities ->
            entities.map { it.toDomain().copy(isSale = false) }
        }
    }

    override fun getOrdersBySeller(sellerId: String): Flow<List<Order>> {
        return remoteDataSource.getOrdersBySellerFlow(sellerId).map { entities ->
            entities.map { it.toDomain().copy(isSale = true) }
        }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit> {
        return try {
            remoteDataSource.updateOrderStatus(orderId, newStatus.name)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    override fun getMessagesForOrder(orderId: String): Flow<List<Message>> {
        // TODO: Implementar consulta a Firestore (Subcolección messages)
        return emptyFlow()
    }
}