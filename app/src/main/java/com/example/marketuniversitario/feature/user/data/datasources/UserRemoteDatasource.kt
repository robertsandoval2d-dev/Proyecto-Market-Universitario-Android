package com.example.marketuniversitario.feature.user.data.datasources

import com.example.marketuniversitario.feature.user.data.models.UserEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

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
        val snapshot = firestore.collection("users").document(userId).get().await()
        return snapshot.toObject(UserEntity::class.java)
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
}
