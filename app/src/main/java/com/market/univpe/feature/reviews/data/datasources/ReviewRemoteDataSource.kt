package com.market.univpe.feature.reviews.data.datasources

import com.market.univpe.feature.reviews.data.models.ReviewEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReviewRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getReviews(productId: String): Flow<List<ReviewEntity>> = callbackFlow {
        val listener = firestore.collection("products")
            .document(productId)
            .collection("reviews")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val reviews = snapshot.toObjects(ReviewEntity::class.java)
                    trySend(reviews).isSuccess
                }
            }

        awaitClose { listener.remove() }
    }

    suspend fun getReview(productId: String, studentId: String): ReviewEntity? {
        val snapshot = firestore.collection("products")
            .document(productId)
            .collection("reviews")
            .document(studentId)
            .get()
            .await()
        return snapshot.toObject(ReviewEntity::class.java)
    }

    suspend fun saveReview(productId: String, reviewEntity: ReviewEntity) {
        val productRef = firestore.collection("products").document(productId)
        val reviewRef = productRef.collection("reviews").document(reviewEntity.id)

        firestore.runTransaction { transaction ->
            // 1. Obtener el producto para saber el businessId
            val productSnapshot = transaction.get(productRef)
            val businessId = productSnapshot.getString("businessId") ?: return@runTransaction

            val businessRef = firestore.collection("businesses").document(businessId)
            val businessSnapshot = transaction.get(businessRef)

            // 2. Ver si la reseña ya existía (Edición) o es nueva
            val existingReviewSnapshot = transaction.get(reviewRef)
            val isEdit = existingReviewSnapshot.exists()

            val currentTotalReviews = businessSnapshot.getLong("totalReviews")?.toInt() ?: 0
            val currentRating = businessSnapshot.getDouble("rating") ?: 0.0
            val newRatingValue = reviewEntity.rating.toDouble()

            var newTotalReviews = currentTotalReviews
            var newAverageRating = currentRating

            if (isEdit) {
                // Lógica de edición
                val oldRatingValue = existingReviewSnapshot.getLong("rating")?.toDouble() ?: 0.0
                if (currentTotalReviews > 0) {
                    newAverageRating = ((currentRating * currentTotalReviews) - oldRatingValue + newRatingValue) / currentTotalReviews
                }
            } else {
                // Lógica de nueva reseña
                newTotalReviews = currentTotalReviews + 1
                newAverageRating = ((currentRating * currentTotalReviews) + newRatingValue) / newTotalReviews
            }

            // 3. Guardar los cambios atómicamente
            transaction.set(reviewRef, reviewEntity)
            transaction.update(
                businessRef,
                mapOf(
                    "totalReviews" to newTotalReviews,
                    "rating" to newAverageRating
                )
            )
            null
        }.await()
    }
}
