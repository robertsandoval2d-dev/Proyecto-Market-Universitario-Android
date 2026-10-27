package com.example.marketuniversitario.feature.admin.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.marketuniversitario.feature.admin.ui.AdminMainRoute
import com.example.marketuniversitario.feature.admin.ui.role_selection.RoleSelectionRoute

fun NavGraphBuilder.adminGraph(navHostController: NavHostController) {
    composable("role_selection") {
        RoleSelectionRoute(
            onNavigateToUserHome = {
                navHostController.navigate("inicio") {
                    popUpTo("role_selection") { inclusive = true }
                }
            },
            onNavigateToAdminManagement = {
                navHostController.navigate("admin_management")
            }
        )
    }

    composable("admin_management") {
        AdminMainRoute(rootNavController = navHostController)
    }
}
