package com.example.marketuniversitario.feature.reviews.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.marketuniversitario.feature.reviews.domain.models.Review

@Entity(tableName = "reviews")
data class ReviewRoomEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val studentId: String,
    val studentName: String,
    val rating: Int,
    val comment: String,
    val createdAt: Long,
    val isSynced: Boolean = true
)

fun Review.toRoomEntity(productId: String, isSynced: Boolean = true) = ReviewRoomEntity(
    id = this.id,
    productId = productId,
    studentId = this.studentId,
    studentName = this.studentName,
    rating = this.rating,
    comment = this.comment,
    createdAt = this.createdAt,
    isSynced = isSynced
)

fun ReviewRoomEntity.toDomain() = Review(
    id = this.id,
    studentId = this.studentId,
    studentName = this.studentName,
    rating = this.rating,
    comment = this.comment,
    createdAt = this.createdAt
)
