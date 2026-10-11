package com.example.marketuniversitario.feature.home.domain.models

data class FeedProduct(
    val id: String = "",
    val businessId: String = "",
    val businessName: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val imageUrl: String = "",
    val isAvailable: Boolean = true,
    val allTags: List<String> = emptyList(),
    val searchKeywords: List<String> = emptyList(),
    val createdAt: Long = 0L,
)