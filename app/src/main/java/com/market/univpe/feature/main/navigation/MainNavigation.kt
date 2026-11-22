package com.market.univpe.feature.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.market.univpe.feature.main.ui.MainRoute

fun NavGraphBuilder.mainGraph(rootNavController: NavHostController) {
    composable("inicio") {
        MainRoute(rootNavController = rootNavController)
    }
}
