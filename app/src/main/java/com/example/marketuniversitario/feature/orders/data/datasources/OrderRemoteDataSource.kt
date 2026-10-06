package com.example.marketuniversitario.feature.orders.data.datasources

import com.example.marketuniversitario.feature.orders.data.models.OrderEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
){
    private val ordersCollection = firestore.collection("orders")
    private val productsCollection = firestore.collection("products")

    suspend fun createOrder(orderEntity: OrderEntity): String {
        val orderDocRef = ordersCollection.document()
        val newOrder = orderEntity.copy(id = orderDocRef.id)
        val productDocRef = productsCollection.document(orderEntity.productId)

        firestore.runTransaction { transaction ->
            val productSnapshot = transaction.get(productDocRef)
            if (!productSnapshot.exists()) {
                throw Exception("El producto no existe")
            }

            val currentAvailable = productSnapshot.getLong("stock")
            val currentReserved = productSnapshot.getLong("reservedStock") ?: 0L

            if (currentAvailable != null) {
                // Producto con stock limitado
                if (currentAvailable < orderEntity.quantity) {
                    throw Exception("Stock insuficiente o producto agotado")
                }

                val newAvailable = currentAvailable - orderEntity.quantity
                val newReserved = currentReserved + orderEntity.quantity
                val isAvailable = newAvailable > 0

                transaction.update(productDocRef, "stock", newAvailable)
                transaction.update(productDocRef, "reservedStock", newReserved)
                transaction.update(productDocRef, "isAvailable", isAvailable)
            } else {
                // Stock ilimitado o indefinido -> Servicios
                val newReserved = currentReserved + orderEntity.quantity
                transaction.update(productDocRef, "reservedStock", newReserved)
            }

            transaction.set(orderDocRef, newOrder)
            null
        }.await()

        return orderDocRef.id
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        ordersCollection.document(orderId).update(
            mapOf(
                "status" to newStatus,
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
    }

    fun getOrdersByBuyerFlow(buyerId: String): Flow<List<OrderEntity>> = callbackFlow {
        val subscription = ordersCollection
            .whereEqualTo("buyerId", buyerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val orders = snapshot.toObjects(OrderEntity::class.java)
                    trySend(orders.sortedByDescending { it.createdAt }).isSuccess
                }
            }
        awaitClose { subscription.remove() }
    }

    fun getOrdersBySellerFlow(sellerId: String): Flow<List<OrderEntity>> = callbackFlow {
        val subscription = ordersCollection
            .whereEqualTo("sellerId", sellerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val orders = snapshot.toObjects(OrderEntity::class.java)
                    trySend(orders.sortedByDescending { it.createdAt }).isSuccess
                }
            }
        awaitClose { subscription.remove() }
    }

    fun getOrderFlow(orderId: String): Flow<OrderEntity?> = callbackFlow {
        val subscription = ordersCollection.document(orderId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val order = snapshot.toObject(OrderEntity::class.java)
                    trySend(order).isSuccess
                } else {
                    trySend(null).isSuccess
                }
            }
        awaitClose { subscription.remove() }
    }
}
