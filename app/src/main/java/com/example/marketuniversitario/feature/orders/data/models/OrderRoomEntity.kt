package com.example.marketuniversitario.feature.orders.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.marketuniversitario.feature.orders.domain.models.Order
import com.example.marketuniversitario.feature.orders.domain.models.OrderStatus

@Entity(tableName = "orders")
data class OrderRoomEntity(
    @PrimaryKey val id: String,
    val buyerId: String,
    val buyerName: String,
    val buyerPhone: String,
    val sellerId: String,
    val businessId: String,
    val businessName: String,
    val productId: String,
    val productName: String,
    val productImage: String,
    val productPrice: Double,
    val quantity: Int,
    val totalPrice: Double,
    val note: String,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isSale: Boolean,
    val isSynced: Boolean = true
)

fun Order.toRoomEntity(isSynced: Boolean = true) = OrderRoomEntity(
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
    updatedAt = this.updatedAt,
    isSale = this.isSale,
    isSynced = isSynced
)

fun OrderRoomEntity.toDomain() = Order(
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
    updatedAt = this.updatedAt,
    isSale = this.isSale
)
