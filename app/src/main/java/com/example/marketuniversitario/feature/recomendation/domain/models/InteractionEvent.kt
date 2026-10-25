package com.example.marketuniversitario.feature.recomendation.domain.models

enum class InteractionType(val weight: Int) {
    VIEW(1),
    FAVORITE(3),
    PURCHASE(5)
}

data class InteractionEvent(
    val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val categoryId: String = "", // Para sumar puntos a la categoría
    val productTags: List<String> = emptyList(), // Para sumar puntos a tags específicos
    val type: String = InteractionType.VIEW.name,
    val weight: Int = InteractionType.VIEW.weight,
    val timestamp: Long = System.currentTimeMillis()
)