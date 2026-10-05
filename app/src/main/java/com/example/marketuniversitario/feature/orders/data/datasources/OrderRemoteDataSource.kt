package com.example.marketuniversitario.feature.orders.data.datasources

import com.example.marketuniversitario.feature.orders.data.models.OrderEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
){
    private val ordersCollection = firestore.collection("orders")

    suspend fun createOrder(orderEntity: OrderEntity): String {
        val docRef = ordersCollection.document()
        val newOrder = orderEntity.copy(id = docRef.id)
        docRef.set(newOrder).await()
        return docRef.id
    }

}