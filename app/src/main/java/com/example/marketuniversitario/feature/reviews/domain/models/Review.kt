package com.example.marketuniversitario.feature.reviews.domain.models

data class Review(
    val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
