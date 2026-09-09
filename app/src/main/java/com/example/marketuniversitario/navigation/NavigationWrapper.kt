package com.example.marketuniversitario.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.example.marketuniversitario.Greeting
import com.example.marketuniversitario.feature.splash.ui.screen.SplashScreen

@Composable
fun NavigationWrapper(
    navHostController: NavHostController
){
    NavHost(navController = navHostController, startDestination = "splash") {
        composable("splash") {
            SplashScreen (
                onSplashFinished = {
                    navHostController.navigate("greeting") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable ("greeting") {
            Greeting(name = "Android")
        }
    }
}