package com.example.marketuniversitario.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.example.marketuniversitario.Greeting
import com.example.marketuniversitario.feature.auth.ui.screen.LoginRoute
import com.example.marketuniversitario.feature.auth.ui.screen.WelcomeScreen
import com.example.marketuniversitario.feature.auth.ui.screen.LoginScreen
import com.example.marketuniversitario.feature.splash.ui.screen.SplashScreen

@Composable
fun NavigationWrapper(
    navHostController: NavHostController
){
    NavHost(navController = navHostController, startDestination = "splash") {
        composable("splash") {
            SplashScreen (
                onSplashFinished = {
                    navHostController.navigate("welcome") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable ("welcome") {
            WelcomeScreen(
                onNavigateToLoginScreen = {
                    navHostController.navigate("login")
                }
            )
        }

        composable ("login") {
            LoginRoute(
                viewModel = viewModel(),

                onLoginSuccess = {
                    navHostController.navigate("inicio"){
                        popUpTo ("welcome"){  inclusive = true }
                    }
                }
            )
        }

        composable ("inicio"){
            Greeting("Android")
        }
    }
}