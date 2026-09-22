package com.example.marketuniversitario.feature.business.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.marketuniversitario.feature.business.ui.business.BusinessRoute
import com.example.marketuniversitario.feature.main.navigation.BottomNavItem

fun NavGraphBuilder.businessGraph(navController: NavHostController) {
    composable("business_tab") {
        BusinessRoute(
            onNavigateToAddProduct = {
                navController.navigate("add_product")
            },
            onHomeScreen = {
                navController.navigate(BottomNavItem.Home.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}
