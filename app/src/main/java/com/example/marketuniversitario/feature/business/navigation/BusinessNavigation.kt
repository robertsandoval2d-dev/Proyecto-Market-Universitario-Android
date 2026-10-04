package com.example.marketuniversitario.feature.business.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.marketuniversitario.feature.business.ui.business.BusinessRoute
import com.example.marketuniversitario.feature.business.ui.manage_item.ManageItemScreen
import com.example.marketuniversitario.feature.main.navigation.BottomNavItem

fun NavGraphBuilder.businessGraph(navController: NavHostController, rootNavController: NavHostController) {
    composable("business_tab") {
        BusinessRoute(
            onHomeScreen = {
                navController.navigate(BottomNavItem.Home.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            onManageProductScreen = { businessId ->
                navController.navigate("manage_item/$businessId/PRODUCT")
            },
            onManageServiceScreen = { businessId ->
                navController.navigate("manage_item/$businessId/SERVICE")
            }
        )
    }

    composable("manage_item/{businessId}/{itemType}") {
        ManageItemScreen(
            onNavigateBack = {
                navController.navigateUp()
            },
            onNavigateToAddItem = { itemType ->
                rootNavController.navigate("edit_item/${itemType.name}")
            },
            onNavigateToEditItem = { product ->
                rootNavController.navigate("edit_item/${product.type}?productId=${product.id}")
            }
        )
    }
}
