package com.example.marketuniversitario.feature.business.domain.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.ui.graphics.vector.ImageVector

enum class BusinessCategory(
    val id: String,
    val displayName: String,
    val icon: ImageVector
) {
    FOOD("FOOD", "Alimentos", Icons.Default.Fastfood),
    SERVICES("SERVICES", "Servicios", Icons.Default.Build),
    DEVICES("DEVICES", "Dispositivos", Icons.Default.Devices),
    HEADPHONES("HEADPHONES", "Audífonos", Icons.Default.Headphones),
    CLOTHING("CLOTHING", "Ropa", Icons.Default.Checkroom),
    BOOKS("BOOKS", "Libros", Icons.AutoMirrored.Filled.MenuBook),
    OTHER("OTHER", "Otros", Icons.Default.Storefront);

    companion object {
        fun fromId(id: String): BusinessCategory {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: OTHER
        }

        fun getAvailableCategories(currentCategoryIds: List<String>): List<BusinessCategory> {
            return entries.filterNot { category ->
                currentCategoryIds.any { id -> id.equals(category.id, ignoreCase = true) }
            }
        }
    }
}
