package com.example.marketuniversitario.feature.reviews.data.models

import com.example.marketuniversitario.feature.reviews.domain.models.Review
import com.google.firebase.firestore.DocumentId

data class ReviewEntity(
    @DocumentId val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

fun Review.toEntity() = ReviewEntity(
    id = this.id,
    studentId = this.studentId,
    studentName = this.studentName,
    rating = this.rating,
    comment = this.comment,
    createdAt = this.createdAt
)

fun ReviewEntity.toDomain() = Review(
    id = this.id,
    studentId = this.studentId,
    studentName = this.studentName,
    rating = this.rating,
    comment = this.comment,
    createdAt = this.createdAt
)
