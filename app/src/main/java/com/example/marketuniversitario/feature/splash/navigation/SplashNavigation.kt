package com.example.marketuniversitario.feature.splash.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.marketuniversitario.feature.splash.ui.SplashRoute

fun NavGraphBuilder.splashGraph(navHostController: NavHostController) {
    composable("splash") {
        SplashRoute(
            viewModel = hiltViewModel(),
            onNavigate = { destination ->
                navHostController.navigate(destination) {
                    popUpTo("splash") { inclusive = true }
                }
            }
        )
    }
}
