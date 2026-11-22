package com.market.univpe.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.market.univpe.feature.admin.navigation.adminGraph
import com.market.univpe.feature.auth.navigation.authGraph
import com.market.univpe.feature.business.ui.edit_item.EditItemScreen
import com.market.univpe.feature.business.ui.public_business.PublicBusinessRoute
import com.market.univpe.feature.business.ui.product_detail.ProductDetailRoute
import com.market.univpe.feature.main.navigation.mainGraph
import com.market.univpe.feature.orders.ui.chat.OrderChatRoute
import com.market.univpe.feature.orders.ui.order_request.OrderRequestDialogRoute
import com.market.univpe.feature.splash.navigation.splashGraph

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
        adminGraph(navHostController)

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

        composable(
            route = "chat/{orderId}",
            arguments = listOf(
                navArgument("orderId") {
                    type = NavType.StringType
                }
            )
        ) {
            OrderChatRoute(
                onBackClick = { navHostController.navigateUp() }
            )
        }

        composable(
            route = "admin_chat/{orderId}",
            arguments = listOf(
                navArgument("orderId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId").orEmpty()
            com.market.univpe.feature.orders.ui.admin_chat.AdminOrderChatRoute(
                orderId = orderId,
                onBackClick = { navHostController.navigateUp() }
            )
        }

        composable(
            route = "view_business/{businessId}",
            arguments = listOf(
                navArgument("businessId") {
                    type = NavType.StringType
                }
            )
        ) {
            PublicBusinessRoute(
                onNavigateBack = { navHostController.navigateUp() },
                onProductClick = { productId ->
                    val prev = navHostController.previousBackStackEntry
                    val cameFromSameProduct =
                        prev?.destination?.route == "product_detail/{productId}" &&
                                prev.arguments?.getString("productId") == productId

                    if (cameFromSameProduct) navHostController.popBackStack()
                    else navHostController.navigate("product_detail/$productId")
                }
            )
        }

        composable(
            route = "product_detail/{productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.StringType
                }
            )
        ) {
            ProductDetailRoute(
                onNavigateBack = { navHostController.navigateUp() },
                onNavigateToBusiness = { businessId ->
                    val prev = navHostController.previousBackStackEntry
                    val cameFromSameBusiness =
                        prev?.destination?.route == "view_business/{businessId}" &&
                                prev.arguments?.getString("businessId") == businessId

                    if (cameFromSameBusiness) navHostController.popBackStack()
                    else navHostController.navigate("view_business/$businessId")
                },
                onRequestOrderClick = { productId  ->
                    navHostController.navigate("request/$productId")
                }
            )
        }
        dialog(
            route = "request/{productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.StringType
                }
            )
        ) {
            OrderRequestDialogRoute(
                onDismiss = { navHostController.popBackStack() }
            )
        }
    }
}
