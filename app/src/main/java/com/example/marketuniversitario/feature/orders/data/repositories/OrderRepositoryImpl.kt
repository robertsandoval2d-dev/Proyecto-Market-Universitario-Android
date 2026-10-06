package com.example.marketuniversitario.feature.orders.data.repositories

import android.net.Uri
import com.example.marketuniversitario.feature.orders.data.datasources.MessageRemoteDataSource
import com.example.marketuniversitario.feature.orders.data.datasources.OrderRemoteDataSource
import com.example.marketuniversitario.feature.orders.data.models.toDomain
import com.example.marketuniversitario.feature.orders.data.models.toEntity
import com.example.marketuniversitario.feature.orders.domain.models.Order
import com.example.marketuniversitario.feature.orders.domain.models.OrderStatus
import com.example.marketuniversitario.feature.orders.domain.models.Message
import com.example.marketuniversitario.feature.orders.domain.repositories.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val remoteDataSource: OrderRemoteDataSource,
    private val messageRemoteDataSource: MessageRemoteDataSource
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



    override fun getOrder(orderId: String): Flow<Order?> {
        return remoteDataSource.getOrderFlow(orderId).map { entity ->
            entity?.toDomain()
        }
    }

    override fun getMessagesForOrder(orderId: String): Flow<List<Message>> {
        return messageRemoteDataSource.getMessages(orderId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun sendTextMessage(message: Message): Result<Unit> {
        return try {
            messageRemoteDataSource.sendTextMessage(message.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendVoiceMessage(message: Message, localAudioUri: Uri): Result<Unit> {
        return try {
            messageRemoteDataSource.sendVoiceMessage(message.toEntity(), localAudioUri)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
