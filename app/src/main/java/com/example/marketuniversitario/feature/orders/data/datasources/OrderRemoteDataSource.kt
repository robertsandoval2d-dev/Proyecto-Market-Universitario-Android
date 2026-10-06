package com.example.marketuniversitario.feature.orders.data.datasources

import com.example.marketuniversitario.feature.orders.data.models.OrderEntity
import com.google.firebase.firestore.FirebaseFirestore
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
}
