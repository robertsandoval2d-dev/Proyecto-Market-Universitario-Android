package com.example.marketuniversitario.feature.main.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val additionalRoutes: List<String> = emptyList(),
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    fun matchesRoute(currentRoute: String?): Boolean {
        if (currentRoute == null) return false
        if (currentRoute == route) return true
        return additionalRoutes.any { currentRoute.startsWith(it) }
    }

    object Home : BottomNavItem(
        route = "feed_tab",
        title = "Inicio",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object Business : BottomNavItem(
        route = "business_tab",
        additionalRoutes = listOf("manage_item"),
        title = "Mi Negocio",
        selectedIcon = Icons.Filled.Storefront,
        unselectedIcon = Icons.Outlined.Storefront
    )

    object Orders : BottomNavItem(
        route = "orders_tab",
        title = "Pedidos",
        selectedIcon = Icons.Filled.LocalMall,
        unselectedIcon = Icons.Outlined.LocalMall
    )

    object Profile : BottomNavItem(
        route = "profile_tab",
        additionalRoutes = listOf("edit_profile", "preferences", "terms","user_reports"),
        title = "Perfil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}
