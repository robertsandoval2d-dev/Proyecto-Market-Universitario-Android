package com.example.marketuniversitario.feature.orders.domain.models

data class ProductRequest(
    val id: String,
    val businessId: String = "",
    val businessName: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val quantity: Int = 1,
    val note: String = ""
)
