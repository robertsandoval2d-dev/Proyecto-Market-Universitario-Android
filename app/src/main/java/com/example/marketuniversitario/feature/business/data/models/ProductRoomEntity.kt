package com.example.marketuniversitario.feature.business.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.marketuniversitario.feature.business.domain.models.Product
import java.util.UUID

@Entity(tableName = "products")
data class ProductRoomEntity(
    @PrimaryKey val id: String,
    val businessId: String,
    val businessName: String,
    val name: String,
    val description: String,
    val price: Double,
    val type: String,
    val category: String,
    val images: String, // Comma-separated list of image URLs
    val stock: Int?,
    val reservedStock: Int? = 0,
    val isAvailable: Boolean,
    val tags: String, // Comma-separated list of tags
    val views: Int,
    val salesCount: Int,
    val createdAt: Long,
    val isSynced: Boolean = false // Para sincronización offline con WorkManager
)

fun Product.toRoomEntity(isSynced: Boolean = false) = ProductRoomEntity(
    id = this.id.ifBlank { UUID.randomUUID().toString() },
    businessId = this.businessId,
    businessName = this.businessName,
    name = this.name,
    description = this.description,
    price = this.price,
    type = this.type,
    category = this.category,
    images = this.images.joinToString(","),
    stock = this.stock,
    reservedStock = this.reservedStock,
    isAvailable = this.isAvailable,
    tags = this.tags.joinToString(","),
    views = this.views,
    salesCount = this.salesCount,
    createdAt = this.createdAt,
    isSynced = isSynced
)

fun ProductRoomEntity.toDomain() = Product(
    id = this.id,
    businessId = this.businessId,
    businessName = this.businessName,
    name = this.name,
    description = this.description,
    price = this.price,
    type = this.type,
    category = this.category,
    images = if (this.images.isBlank()) emptyList() else this.images.split(","),
    stock = this.stock,
    reservedStock = this.reservedStock,
    isAvailable = this.isAvailable,
    tags = if (this.tags.isBlank()) emptyList() else this.tags.split(","),
    views = this.views,
    salesCount = this.salesCount,
    createdAt = this.createdAt
)
