package com.example.marketuniversitario.feature.reviews.data.datasources

import com.example.marketuniversitario.feature.reviews.data.models.ReviewEntity
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
        firestore.collection("products")
            .document(productId)
            .collection("reviews")
            .document(reviewEntity.id)
            .set(reviewEntity)
            .await()
    }
}
