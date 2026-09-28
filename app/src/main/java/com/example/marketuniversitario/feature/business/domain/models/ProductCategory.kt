package com.example.marketuniversitario.feature.business.domain.models

enum class ProductCategory(
    val id: String,
    val displayName: String
) {
    TECHNOLOGY("TECHNOLOGY", "Tecnología"),
    FOOD("FOOD", "Comidas y Snacks"),
    CLOTHING("CLOTHING", "Ropa y Accesorios"),
    BOOKS("BOOKS", "Libros y Apuntes"),
    SERVICES("SERVICES", "Servicios Profesionales"),
    STATIONERY("STATIONERY", "Artículos de Oficina"),
    OTHER("OTHER", "Otros");

    companion object {
        fun fromId(id: String): ProductCategory {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: OTHER
        }
    }
}
