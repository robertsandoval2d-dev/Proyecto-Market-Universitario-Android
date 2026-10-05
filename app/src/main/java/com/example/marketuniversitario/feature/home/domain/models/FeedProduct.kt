package com.example.marketuniversitario.feature.home.domain.models

data class FeedProduct(
    val id: String,
    val businessId: String = "",
    val businessName: String,
    val name: String,
    val price: Double,
    val imageUrl: String
)
