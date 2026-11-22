package com.market.univpe.feature.user.data.datasources

import com.market.univpe.feature.user.data.models.UserEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class UserRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun saveUser(userEntity: UserEntity) {
        firestore.collection("users")
            .document(userEntity.id)
            .set(userEntity)
            .await()
    }

    fun getUserStream(userId: String): Flow<UserEntity?> {
        return firestore.collection("users")
            .document(userId)
            .snapshots()
            .map { it.toObject(UserEntity::class.java) }
    }

    suspend fun getUser(userId: String): UserEntity? {
        return try {
            // Intentar servidor con un límite de 3 segundos. Si expira o falla, leer de la caché local de Firestore.
            withTimeoutOrNull(3000.milliseconds) {
                firestore.collection("users").document(userId).get().await()
            }?.toObject(UserEntity::class.java)
                ?: firestore.collection("users").document(userId).get(Source.CACHE).await()
                    .toObject(UserEntity::class.java)
        } catch (e: Exception) {
            try {
                firestore.collection("users").document(userId).get(Source.CACHE).await()
                    .toObject(UserEntity::class.java)
            } catch (cacheEx: Exception) {
                null
            }
        }
    }

    suspend fun updateUserBusinessStatus(userId: String, hasBusiness: Boolean, businessId: String?) {
        firestore.collection("users")
            .document(userId)
            .update(
                mapOf(
                    "hasBusiness" to hasBusiness,
                    "businessId" to businessId
                )
            )
            .await()
    }

    suspend fun updateUserProfile(userEntity: UserEntity) {
        firestore.collection("users")
            .document(userEntity.id)
            .set(userEntity)
            .await()
    }

    suspend fun applyUserSanction(
        userId: String,
        status: String,
        suspendedUntil: Long?,
        reason: String
    ) {
        val userDoc = firestore.collection("users").document(userId).get().await()
        val businessId = userDoc.getString("businessId")

        val batch = firestore.batch()

        val userRef = firestore.collection("users").document(userId)
        batch.update(
            userRef,
            mapOf(
                "accountStatus" to status,
                "suspendedUntil" to suspendedUntil,
                "suspensionReason" to reason,
                "updatedAt" to System.currentTimeMillis()
            )
        )

        // Si el usuario sancionado tiene un negocio, desactivar el negocio y todos sus productos (isAvailable = false)
        if (!businessId.isNullOrBlank()) {
            val businessRef = firestore.collection("businesses").document(businessId)
            batch.update(businessRef, "isActive", false)

            val productsSnapshot = firestore.collection("products")
                .whereEqualTo("businessId", businessId)
                .get()
                .await()

            for (doc in productsSnapshot.documents) {
                batch.update(doc.reference, "isAvailable", false)
            }
        }

        batch.commit().await()
    }

    suspend fun restoreUserAccount(userId: String) {
        val userDoc = firestore.collection("users").document(userId).get().await()
        val businessId = userDoc.getString("businessId")

        val batch = firestore.batch()

        val userRef = firestore.collection("users").document(userId)
        batch.update(
            userRef,
            mapOf(
                "accountStatus" to "active",
                "suspendedUntil" to null,
                "suspensionReason" to "",
                "updatedAt" to System.currentTimeMillis()
            )
        )

        // Si el usuario tiene un negocio, reactivar el negocio y todos sus productos (isAvailable = true)
        if (!businessId.isNullOrBlank()) {
            val businessRef = firestore.collection("businesses").document(businessId)
            batch.update(businessRef, "isActive", true)

            val productsSnapshot = firestore.collection("products")
                .whereEqualTo("businessId", businessId)
                .get()
                .await()

            for (doc in productsSnapshot.documents) {
                batch.update(doc.reference, "isAvailable", true)
            }
        }

        batch.commit().await()
    }

    fun getAllUsers(): Flow<List<UserEntity>> {
        return firestore.collection("users")
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(UserEntity::class.java) }
            }
    }
}
