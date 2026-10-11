package com.example.marketuniversitario.feature.reviews.data.datasources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.marketuniversitario.feature.reviews.data.models.ReviewRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY createdAt DESC")
    fun getReviews(productId: String): Flow<List<ReviewRoomEntity>>

    @Query("SELECT * FROM reviews WHERE productId = :productId AND studentId = :studentId")
    suspend fun getReviewByUser(productId: String, studentId: String): ReviewRoomEntity?

    @Query("SELECT * FROM reviews WHERE isSynced = 0")
    suspend fun getUnsyncedReviews(): List<ReviewRoomEntity>

    @Query("UPDATE reviews SET isSynced = 1 WHERE id = :reviewId")
    suspend fun markAsSynced(reviewId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewRoomEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewRoomEntity)
}
