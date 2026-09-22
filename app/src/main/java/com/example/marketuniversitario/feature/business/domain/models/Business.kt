package com.example.marketuniversitario.feature.business.domain.models

data class Business(
    val id: String = "",
    val ownerId: String = "", // Para cruzar con la colección User
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val bannerUrl: String? = null,
    val isActive: Boolean = true,

    // Campos preparados para IA (Métricas de confianza del negocio)
    val rating: Double = 0.0,
    val totalReviews: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)