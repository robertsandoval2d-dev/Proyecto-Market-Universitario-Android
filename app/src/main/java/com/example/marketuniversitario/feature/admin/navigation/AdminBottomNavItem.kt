package com.example.marketuniversitario.feature.admin.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AdminBottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Dashboard : AdminBottomNavItem(
        route = "admin_dashboard_tab",
        title = "Dashboard",
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard
    )

    object Module2 : AdminBottomNavItem(
        route = "admin_module2_tab",
        title = "Módulo 2",
        selectedIcon = Icons.Filled.Storefront,
        unselectedIcon = Icons.Outlined.Storefront
    )

    object Reports : AdminBottomNavItem(
        route = "admin_reports_tab",
        title = "Incidencias",
        selectedIcon = Icons.Filled.Report,
        unselectedIcon = Icons.Outlined.Report
    )

    object Module4 : AdminBottomNavItem(
        route = "admin_module4_tab",
        title = "Módulo 4",
        selectedIcon = Icons.Filled.AdminPanelSettings,
        unselectedIcon = Icons.Outlined.AdminPanelSettings
    )
}
