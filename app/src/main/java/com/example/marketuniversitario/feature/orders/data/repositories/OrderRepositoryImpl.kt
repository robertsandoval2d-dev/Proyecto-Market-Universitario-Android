package com.example.marketuniversitario.feature.orders.data.repositories

import android.content.Context
import android.net.Uri
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.marketuniversitario.feature.orders.data.datasources.MessageLocalDataSource
import com.example.marketuniversitario.feature.orders.data.datasources.MessageRemoteDataSource
import com.example.marketuniversitario.feature.orders.data.datasources.OrderLocalDataSource
import com.example.marketuniversitario.feature.orders.data.datasources.OrderRemoteDataSource
import com.example.marketuniversitario.feature.orders.data.models.toDomain
import com.example.marketuniversitario.feature.orders.data.models.toEntity
import com.example.marketuniversitario.feature.orders.data.models.toRoomEntity
import com.example.marketuniversitario.feature.orders.data.workers.SyncOrderWorker
import com.example.marketuniversitario.feature.orders.data.workers.SyncMessageWorker
import com.example.marketuniversitario.feature.orders.domain.models.Order
import com.example.marketuniversitario.feature.orders.domain.models.OrderStatus
import com.example.marketuniversitario.feature.orders.domain.models.Message
import com.example.marketuniversitario.feature.orders.domain.repositories.OrderRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val remoteDataSource: OrderRemoteDataSource,
    private val messageRemoteDataSource: MessageRemoteDataSource,
    private val localDataSource: OrderLocalDataSource,
    private val messageLocalDataSource: MessageLocalDataSource
) : OrderRepository {

    override suspend fun getOrders(): Result<List<Order>> {
        TODO("Not yet implemented")
    }

    override suspend fun createOrder(order: Order): Result<Order> {
        return try {
            val entity = order.toEntity()
            val generatedId = remoteDataSource.createOrder(entity)
            val createdOrder = order.copy(id = generatedId, isSale = false)
            // Guardar localmente solo después de recibir el visto bueno de la nube
            localDataSource.insertOrder(createdOrder.toRoomEntity(isSynced = true))
            Result.success(createdOrder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getOrdersByBuyer(buyerId: String): Flow<List<Order>> {
        // Sincronizar remotamente a SQLite en segundo plano si hay conexión
        CoroutineScope(Dispatchers.IO).launch {
            try {
                remoteDataSource.getOrdersByBuyerFlow(buyerId).collect { entities ->
                    val roomEntities = entities.map { it.toDomain().copy(isSale = false).toRoomEntity(isSynced = true) }
                    localDataSource.insertOrders(roomEntities)
                }
            } catch (_: Exception) {}
        }
        // Consultar siempre desde la base de datos local SQLite (Offline-first)
        return localDataSource.getOrdersByBuyer(buyerId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getOrdersBySeller(sellerId: String): Flow<List<Order>> {
        // Sincronizar remotamente a SQLite en segundo plano si hay conexión
        CoroutineScope(Dispatchers.IO).launch {
            try {
                remoteDataSource.getOrdersBySellerFlow(sellerId).collect { entities ->
                    val roomEntities = entities.map { it.toDomain().copy(isSale = true).toRoomEntity(isSynced = true) }
                    localDataSource.insertOrders(roomEntities)
                }
            } catch (_: Exception) {}
        }
        // Consultar siempre desde la base de datos local SQLite (Offline-first)
        return localDataSource.getOrdersBySeller(sellerId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit> {
        return try {
            val localOrder = localDataSource.getOrderById(orderId)
                ?: return Result.failure(Exception("Pedido no encontrado localmente"))

            // 1. Actualizar localmente primero (Offline-First / Optimistic Update) con isSynced = false
            val updatedOrder = localOrder.copy(
                status = newStatus.name,
                updatedAt = System.currentTimeMillis(),
                isSynced = false
            )
            localDataSource.insertOrder(updatedOrder)

            // 2. Programar WorkManager para sincronizar en segundo plano cuando haya conexión
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SyncOrderWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "sync_orders_work",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                workRequest
            )

            // 3. Retornar éxito inmediatamente a la UI
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
        // Sincronizar mensajes en segundo plano si hay conexión
        CoroutineScope(Dispatchers.IO).launch {
            try {
                messageRemoteDataSource.getMessages(orderId).collect { entities ->
                    val roomEntities = entities.map { it.toDomain().toRoomEntity(isSynced = true) }
                    messageLocalDataSource.insertMessages(roomEntities)
                }
            } catch (_: Exception) {}
        }
        // Leer siempre desde SQLite local (Offline-first)
        return messageLocalDataSource.getMessagesForOrder(orderId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun sendTextMessage(message: Message): Result<Unit> {
        return try {
            // Guardar localmente con isSynced = false
            val roomEntity = message.toRoomEntity(isSynced = false)
            messageLocalDataSource.insertMessage(roomEntity)

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SyncMessageWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "sync_messages_work",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                workRequest
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendVoiceMessage(message: Message, localAudioUri: Uri): Result<Unit> {
        return try {
            // Guardar localmente con la URI local del archivo de audio y isSynced = false
            val roomEntity = message.copy(audioUrl = localAudioUri.toString()).toRoomEntity(isSynced = false)
            messageLocalDataSource.insertMessage(roomEntity)

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SyncMessageWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "sync_messages_work",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                workRequest
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
