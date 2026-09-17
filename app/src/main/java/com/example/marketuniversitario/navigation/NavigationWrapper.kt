package com.example.marketuniversitario.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.example.marketuniversitario.Greeting
import com.example.marketuniversitario.feature.auth.ui.screen.LoginRoute
import com.example.marketuniversitario.feature.auth.ui.screen.WelcomeScreen
import com.example.marketuniversitario.feature.auth.ui.screen.LoginScreen
import com.example.marketuniversitario.feature.auth.ui.screen.SignUpRoute
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
                },
                onNavigateToSignUpScreen = {
                    navHostController.navigate("signup")
                }
            )
        }

        composable ("signup"){
            SignUpRoute(
                viewModel = hiltViewModel(),
                onRegisterSuccess = {
                    navHostController.navigate("login")
                },
                onNavigateBack = {
                    navHostController.navigateUp()
                }
            )
        }

        composable ("login") {
            LoginRoute(
                viewModel = hiltViewModel(),

                onLoginSuccess = {
                    navHostController.navigate("inicio"){
                        popUpTo ("welcome"){  inclusive = true }
                    }
                },
                onNavigateBack = {
                    navHostController.navigateUp()
                }
            )
        }

        composable ("inicio"){
            Greeting("Android")
        }
    }
}