package com.example.marketuniversitario.feature.analytics.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.marketuniversitario.feature.analytics.ui.users.UsersAnalyticsScreen
import com.example.marketuniversitario.feature.analytics.ui.businesses.BusinessesAnalyticsScreen
import com.example.marketuniversitario.feature.analytics.ui.products.ProductsAnalyticsScreen
import com.example.marketuniversitario.feature.analytics.ui.reports.ReportsAnalyticsScreen

fun NavGraphBuilder.analyticsGraph(navController: NavController) {
    navigation(startDestination = "analytics_users", route = "analytics_graph") {
        composable("analytics_users") {
            UsersAnalyticsScreen { navController.navigateUp() }
        }
        composable("analytics_businesses") {
            BusinessesAnalyticsScreen { navController.navigateUp() }
        }
        composable("analytics_products") {
            ProductsAnalyticsScreen { navController.navigateUp() }
        }
        composable("analytics_reports") {
            ReportsAnalyticsScreen { navController.navigateUp() }
        }
    }
}
