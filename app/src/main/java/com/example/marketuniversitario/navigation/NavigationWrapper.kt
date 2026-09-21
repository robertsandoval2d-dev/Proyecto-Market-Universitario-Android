package com.example.marketuniversitario.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.marketuniversitario.feature.auth.navigation.authGraph
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
    }
}
