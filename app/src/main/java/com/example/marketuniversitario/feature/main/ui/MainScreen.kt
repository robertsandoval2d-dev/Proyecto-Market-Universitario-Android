package com.example.marketuniversitario.feature.main.ui

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.marketuniversitario.feature.business.navigation.businessGraph
import com.example.marketuniversitario.feature.home.ui.home.HomeRoute
import com.example.marketuniversitario.feature.main.navigation.BottomNavItem
import com.example.marketuniversitario.feature.main.ui.util.BottomNavigationBar

@Composable
fun MainRoute() {
    MainScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isHome = currentRoute == BottomNavItem.Home.route

    Scaffold(
        topBar = {
            if (currentRoute != BottomNavItem.Home.route) {
                val title = when (currentRoute) {
                    BottomNavItem.Business.route -> "Mi Negocio"
                    BottomNavItem.Orders.route -> "Mis Pedidos"
                    BottomNavItem.Profile.route -> "Mi Perfil"
                    else -> ""
                }

                CenterAlignedTopAppBar(
                    title = { Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    ) },
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
            businessGraph(bottomNavController)
            composable(BottomNavItem.Orders.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Pantalla de Pedidos")
                }
            }
            composable(BottomNavItem.Profile.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Pantalla de Perfil")
                }
            }
        }
    }
}
