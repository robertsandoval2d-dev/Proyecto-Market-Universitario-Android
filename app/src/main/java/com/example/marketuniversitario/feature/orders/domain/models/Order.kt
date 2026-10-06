package com.example.marketuniversitario.feature.orders.domain.models

data class Order(
    val id: String = "",

    // Cliente / Comprador
    val buyerId: String = "",
    val buyerName: String = "",
    val buyerPhone: String = "",

    // Vendedor
    val sellerId: String = "",
    val businessId: String = "",
    val businessName: String = "",

    // Producto
    val productId: String = "",
    val productName: String = "",
    val productImage: String = "",
    val productPrice: Double = 0.0,

    // Solicitud
    val quantity: Int = 1,
    val totalPrice: Double = 0.0,
    val note: String = "",

    val status: OrderStatus = OrderStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSale: Boolean = false
)
