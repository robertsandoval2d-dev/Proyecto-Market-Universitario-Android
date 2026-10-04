package com.example.marketuniversitario.feature.business.data.datasources

import com.example.marketuniversitario.feature.business.data.models.BusinessEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

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
        return try {
            // Intentar servidor con un límite de 3 segundos. Si expira o falla, leer de la caché local de Firestore.
            withTimeoutOrNull(3000.milliseconds) {
                firestore.collection("businesses").document(businessId).get().await()
            }?.toObject(BusinessEntity::class.java)
                ?: firestore.collection("businesses").document(businessId).get(Source.CACHE).await()
                    .toObject(BusinessEntity::class.java)
        } catch (e: Exception) {
            try {
                firestore.collection("businesses").document(businessId).get(Source.CACHE).await()
                    .toObject(BusinessEntity::class.java)
            } catch (cacheEx: Exception) {
                null
            }
        }
    }

    suspend fun updateBusiness(businessEntity: BusinessEntity) {
        firestore.collection("businesses")
            .document(businessEntity.id)
            .set(businessEntity)
            .await()
    }
}
