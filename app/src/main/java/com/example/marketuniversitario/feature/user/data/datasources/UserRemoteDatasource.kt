package com.example.marketuniversitario.feature.user.data.datasources

import com.example.marketuniversitario.feature.user.data.models.UserEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
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
}
