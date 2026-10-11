package com.example.marketuniversitario.feature.business.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.marketuniversitario.feature.business.domain.models.Business
import java.util.UUID

@Entity(tableName = "businesses")
data class BusinessRoomEntity(
    @PrimaryKey val id: String,
    val ownerId: String,
    val name: String,
    val description: String,
    val categories: String, // Comma-separated list of categories
    val bannerUrl: String?,
    val isActive: Boolean,
    val rating: Double,
    val totalReviews: Int,
    val createdAt: Long,
    val isSynced: Boolean = false // Para sincronización offline con WorkManager
)

fun Business.toRoomEntity(isSynced: Boolean = false) = BusinessRoomEntity(
    id = this.id.ifBlank { UUID.randomUUID().toString() },
    ownerId = this.ownerId,
    name = this.name,
    description = this.description,
    categories = this.categories.joinToString(","),
    bannerUrl = this.bannerUrl,
    isActive = this.isActive,
    rating = this.rating,
    totalReviews = this.totalReviews,
    createdAt = this.createdAt,
    isSynced = isSynced
)

fun BusinessRoomEntity.toDomain() = Business(
    id = this.id,
    ownerId = this.ownerId,
    name = this.name,
    description = this.description,
    categories = if (this.categories.isBlank()) emptyList() else this.categories.split(","),
    bannerUrl = this.bannerUrl,
    isActive = this.isActive,
    rating = this.rating,
    totalReviews = this.totalReviews,
    createdAt = this.createdAt
)
