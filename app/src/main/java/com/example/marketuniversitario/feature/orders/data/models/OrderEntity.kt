package com.example.marketuniversitario.feature.orders.data.models

import com.example.marketuniversitario.feature.orders.domain.models.Order
import com.example.marketuniversitario.feature.orders.domain.models.OrderStatus
import com.google.firebase.firestore.DocumentId

data class OrderEntity(
    @DocumentId val id: String = "",
    val buyerId: String = "",
    val buyerName: String = "",
    val buyerPhone: String = "",

    val sellerId: String = "",
    val businessId: String = "",
    val businessName: String = "",

    val productId: String = "",
    val productName: String = "",
    val productImage: String = "",
    val productPrice: Double = 0.0,

    val quantity: Int = 1,
    val totalPrice: Double = 0.0,
    val note: String = "",

    val status: String = OrderStatus.PENDING.displayName,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

fun Order.toEntity() = OrderEntity(
    id = this.id,
    buyerId = this.buyerId,
    buyerName = this.buyerName,
    buyerPhone = this.buyerPhone,
    sellerId = this.sellerId,
    businessId = this.businessId,
    businessName = this.businessName,
    productId = this.productId,
    productName = this.productName,
    productImage = this.productImage,
    productPrice = this.productPrice,
    quantity = this.quantity,
    totalPrice = this.totalPrice,
    note = this.note,
    status = this.status.name,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

fun OrderEntity.toDomain() = Order(
    id = this.id,
    buyerId = this.buyerId,
    buyerName = this.buyerName,
    buyerPhone = this.buyerPhone,
    sellerId = this.sellerId,
    businessId = this.businessId,
    businessName = this.businessName,
    productId = this.productId,
    productName = this.productName,
    productImage = this.productImage,
    productPrice = this.productPrice,
    quantity = this.quantity,
    totalPrice = this.totalPrice,
    note = this.note,
    status = try { OrderStatus.valueOf(this.status) } catch (_: Exception) { OrderStatus.PENDING },
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)
