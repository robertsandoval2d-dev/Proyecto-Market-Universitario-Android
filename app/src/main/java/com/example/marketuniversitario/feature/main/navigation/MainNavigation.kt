package com.example.marketuniversitario.feature.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.marketuniversitario.feature.main.ui.MainRoute

fun NavGraphBuilder.mainGraph(navHostController: NavHostController) {
    composable("inicio") {
        MainRoute()
    }
}
