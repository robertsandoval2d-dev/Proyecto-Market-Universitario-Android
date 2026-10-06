package com.example.marketuniversitario.feature.main.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.marketuniversitario.feature.business.navigation.businessGraph
import com.example.marketuniversitario.feature.home.ui.home.HomeRoute
import com.example.marketuniversitario.feature.main.navigation.BottomNavItem
import com.example.marketuniversitario.feature.main.ui.util.BottomNavigationBar
import com.example.marketuniversitario.feature.orders.ui.orders.OrdersRoute
import com.example.marketuniversitario.feature.user.navigation.profileGraph

@Composable
fun MainRoute(rootNavController: NavHostController) {
    MainScreen(rootNavController = rootNavController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    rootNavController: NavHostController
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isHome = currentRoute == BottomNavItem.Home.route

    Scaffold(
        topBar = {
            val isBusinessRoute = currentRoute == BottomNavItem.Business.route ||
                    currentRoute?.startsWith("manage_item") == true

            if (currentRoute != BottomNavItem.Home.route) {
                val title = when {
                    isBusinessRoute -> "Mi Negocio"
                    currentRoute == BottomNavItem.Orders.route -> "Mis Pedidos"
                    currentRoute == BottomNavItem.Profile.route -> "Mi Perfil"
                    else -> ""
                }

                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(navController = bottomNavController)
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(
                top = if (isHome) 0.dp else innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {
            composable(BottomNavItem.Home.route) {
                HomeRoute()
            }
            businessGraph(bottomNavController, rootNavController)
            composable(BottomNavItem.Orders.route) {
                OrdersRoute(
                    onNavigateToChat = { orderId ->
                        rootNavController.navigate("chat/$orderId")
                    }
                )
            }
            profileGraph(
                navController = bottomNavController,
                onNavigateToEditProfile = { bottomNavController.navigate("edit_profile") },
                onNavigateToPreferences = { bottomNavController.navigate("preferences") },
                onNavigateToTerms = { bottomNavController.navigate("terms") },
                onLogoutSuccess = { rootNavController.navigate("welcome") { popUpTo(0) } }
            )
        }
    }
}
