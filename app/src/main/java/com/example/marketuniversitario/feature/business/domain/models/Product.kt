package com.example.marketuniversitario.feature.business.domain.models

enum class ItemType { PRODUCT, SERVICE }

data class Product(
    val id: String = "",

    val businessId: String = "",
    val businessName: String = "",
    val businessLogoUrl: String? = null,

    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val type: String = ItemType.PRODUCT.name,
    val category: String = "",
    val images: List<String> = emptyList(),
    val stock: Int? = null,
    val isAvailable: Boolean = true,

    // --- CAMPOS CLAVE PARA SISTEMAS DE RECOMENDACIÓN IA ---

    // 1. Filtrado Basado en Contenido (Content-Based) y NLP
    val tags: List<String> = emptyList(),

    // 2. Filtrado Colaborativo (Métricas implícitas de interacción)
    val views: Int = 0,
    val salesCount: Int = 0,
    val favoritesCount: Int = 0,
    val rating: Double = 0.0,

    val createdAt: Long = System.currentTimeMillis()
)