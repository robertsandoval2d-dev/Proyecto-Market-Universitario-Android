package com.example.marketuniversitario.feature.business.data.models

import com.example.marketuniversitario.feature.business.domain.models.Product
import com.google.firebase.firestore.DocumentId

data class ProductEntity(
    @DocumentId val id: String = "",
    val businessId: String = "",
    val businessName: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val type: String = "",
    val category: String = "",
    val images: List<String> = emptyList(),
    val stock: Int? = null,
    val isAvailable: Boolean = true,
    val tags: List<String> = emptyList(),
    val views: Int = 0,
    val salesCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

fun Product.toEntity() = ProductEntity(
    id = this.id,
    businessId = this.businessId,
    businessName = this.businessName,
    name = this.name,
    description = this.description,
    price = this.price,
    type = this.type,
    category = this.category,
    images = this.images,
    stock = this.stock,
    isAvailable = this.isAvailable,
    tags = this.tags,
    views = this.views,
    salesCount = this.salesCount,
    createdAt = this.createdAt
)

fun ProductEntity.toDomain() = Product(
    id = this.id,
    businessId = this.businessId,
    businessName = this.businessName,
    name = this.name,
    description = this.description,
    price = this.price,
    type = this.type,
    category = this.category,
    images = this.images,
    stock = this.stock,
    isAvailable = this.isAvailable,
    tags = this.tags,
    views = this.views,
    salesCount = this.salesCount,
    createdAt = this.createdAt
)
