package com.example.marketuniversitario.feature.business.data.datasources

import com.example.marketuniversitario.feature.business.data.models.BusinessEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BusinessRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun createBusiness(businessEntity: BusinessEntity): String {
        val docRef = if (businessEntity.id.isNotBlank()) {
            firestore.collection("businesses").document(businessEntity.id)
        } else {
            firestore.collection("businesses").document()
        }

        val entityToSave = businessEntity.copy(id = docRef.id)
        docRef.set(entityToSave).await()
        return docRef.id
    }

    suspend fun getBusiness(businessId: String): BusinessEntity? {
        val snapshot = firestore.collection("businesses").document(businessId).get().await()
        return snapshot.toObject(BusinessEntity::class.java)
    }
}
