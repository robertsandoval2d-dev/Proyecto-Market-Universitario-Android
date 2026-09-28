package com.example.marketuniversitario.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.marketuniversitario.feature.auth.navigation.authGraph
import com.example.marketuniversitario.feature.business.ui.edit_item.EditItemScreen
import com.example.marketuniversitario.feature.main.navigation.mainGraph
import com.example.marketuniversitario.feature.splash.navigation.splashGraph

@Composable
fun NavigationWrapper(
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = "splash"
    ) {
        splashGraph(navHostController)
        authGraph(navHostController)
        mainGraph(navHostController)

        composable(
            route = "edit_item/{itemType}?productId={productId}",
            arguments = listOf(
                navArgument("itemType"){
                    type = NavType.StringType
                },
                navArgument("productId"){
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            EditItemScreen(
                onNavigateBack = { navHostController.navigateUp() }
            )
        }
    }
}
