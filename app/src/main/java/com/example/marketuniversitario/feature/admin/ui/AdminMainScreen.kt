package com.example.marketuniversitario.feature.admin.ui

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.marketuniversitario.feature.admin.navigation.AdminBottomNavItem
import com.example.marketuniversitario.feature.admin.ui.ScreenExamples.AdminDashboardScreen
import com.example.marketuniversitario.feature.admin.ui.ScreenExamples.AdminModule2Screen
import com.example.marketuniversitario.feature.admin.ui.ScreenExamples.AdminModule4Screen
import com.example.marketuniversitario.feature.admin.ui.ScreenExamples.AdminReportsScreen
import com.example.marketuniversitario.feature.admin.ui.util.AdminBottomNavigationBar

@Composable
fun AdminMainRoute( rootNavController: NavHostController) {
    AdminMainScreen(rootNavController = rootNavController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(
    rootNavController: NavHostController
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isDashboard = currentRoute == AdminBottomNavItem.Dashboard.route

    Scaffold(
        topBar = {
            if (!isDashboard) {
                val title = when (currentRoute) {
                    AdminBottomNavItem.Module2.route -> "Módulo 2"
                    AdminBottomNavItem.Reports.route -> "Incidencias"
                    AdminBottomNavItem.Module4.route -> "Módulo 4"
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
            AdminBottomNavigationBar(navController = bottomNavController)
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = AdminBottomNavItem.Dashboard.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = if (isDashboard) 0.dp else innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            composable(AdminBottomNavItem.Dashboard.route) {
                AdminDashboardScreen(
                    onSwitchToUserMode = {
                        rootNavController.navigate("inicio") {
                            popUpTo("role_selection") { inclusive = true }
                        }
                    }
                )
            }

//            DashboardGraph(bottomNavController, rootNavController)
//            SecondSectionGraph(bottomNavController)
//            IncidentsGraph(bottomNavController)
//            WAAZAAGraph(bottomNavController)

            composable(AdminBottomNavItem.Module2.route) {
                AdminModule2Screen()
            }

            composable(AdminBottomNavItem.Reports.route) {
                AdminReportsScreen()
            }

            composable(AdminBottomNavItem.Module4.route) {
                AdminModule4Screen()
            }
        }
    }
}
