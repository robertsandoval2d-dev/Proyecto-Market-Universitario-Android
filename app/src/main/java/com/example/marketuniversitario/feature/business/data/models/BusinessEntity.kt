package com.example.marketuniversitario.feature.business.data.models

import com.example.marketuniversitario.feature.business.domain.models.Business
import com.google.firebase.firestore.DocumentId

data class BusinessEntity(
    @DocumentId val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val bannerUrl: String? = null,
    val isActive: Boolean = true,
    val rating: Double = 0.0,
    val totalReviews: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

fun Business.toEntity() = BusinessEntity(
    id = this.id,
    ownerId = this.ownerId,
    name = this.name,
    description = this.description,
    category = this.category,
    bannerUrl = this.bannerUrl,
    isActive = this.isActive,
    rating = this.rating,
    totalReviews = this.totalReviews,
    createdAt = this.createdAt
)

fun BusinessEntity.toDomain() = Business(
    id = this.id,
    ownerId = this.ownerId,
    name = this.name,
    description = this.description,
    category = this.category,
    bannerUrl = this.bannerUrl,
    isActive = this.isActive,
    rating = this.rating,
    totalReviews = this.totalReviews,
    createdAt = this.createdAt
)
