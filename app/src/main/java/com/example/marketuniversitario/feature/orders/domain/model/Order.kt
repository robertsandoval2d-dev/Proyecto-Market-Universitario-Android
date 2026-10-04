package com.example.marketuniversitario.feature.orders.domain.model

data class Order(
    val id: String,
    val productName: String,
    val imageUrl: String,
    val price: Double,
    val quantity: Int,
    val status: OrderStatus,
    val date: String,
    val isSale: Boolean
)