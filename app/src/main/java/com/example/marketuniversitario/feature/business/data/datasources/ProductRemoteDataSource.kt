package com.example.marketuniversitario.feature.business.data.datasources

import com.example.marketuniversitario.feature.business.data.models.ProductEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun createProduct(productEntity: ProductEntity): String {
        val docRef = if (productEntity.id.isNotBlank()) {
            firestore.collection("products").document(productEntity.id)
        } else {
            firestore.collection("products").document()
        }

        val entityToSave = productEntity.copy(id = docRef.id)
        docRef.set(entityToSave).await()
        return docRef.id
    }

    suspend fun getProduct(productId: String): ProductEntity? {
        val snapshot = firestore.collection("products").document(productId).get().await()
        return snapshot.toObject(ProductEntity::class.java)
    }

    suspend fun getProductsByBusiness(businessId: String): List<ProductEntity> {
        val snapshot = firestore.collection("products")
            .whereEqualTo("businessId", businessId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(ProductEntity::class.java) }
    }

    suspend fun getProducts(): List<ProductEntity> {
        val snapshot = firestore.collection("products").get().await()
        return snapshot.documents.mapNotNull { it.toObject(ProductEntity::class.java) }
    }

    suspend fun updateProduct(productEntity: ProductEntity) {
        firestore.collection("products")
            .document(productEntity.id)
            .set(productEntity)
            .await()
    }

    suspend fun deleteProduct(productId: String) {
        firestore.collection("products")
            .document(productId)
            .delete()
            .await()
    }
}
