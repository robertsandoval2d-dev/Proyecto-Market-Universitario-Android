package com.example.marketuniversitario.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.example.marketuniversitario.Greeting
import com.example.marketuniversitario.feature.auth.ui.login.LoginRoute
import com.example.marketuniversitario.feature.auth.ui.sign_up.SignUpRoute
import com.example.marketuniversitario.feature.auth.ui.welcome.WelcomeRoute
import com.example.marketuniversitario.feature.splash.ui.screen.SplashScreen
import com.example.marketuniversitario.feature.auth.ui.forgot_password.ForgotPasswordRoute

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
            WelcomeRoute(
                viewModel = hiltViewModel(),
                onNavigateToLoginScreen = {
                    navHostController.navigate("login")
                },
                onNavigateToSignUpScreen = {
                    navHostController.navigate("signup")
                },
                onLoginSuccess = {
                    navHostController.navigate("inicio") {
                        popUpTo("welcome") { inclusive = true }
                    }
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
                },
                onNavigateHome = {
                    navHostController.navigate("inicio"){
                        popUpTo("welcome"){  inclusive = true }
                    }
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
                onNavigateToForgotPassword = { // <--- NUEVO
                    navHostController.navigate("forgot_password")
                },
                onNavigateBack = {
                    navHostController.navigateUp()
                }
            )
        }

        composable("forgot_password") {
            ForgotPasswordRoute(
                viewModel = hiltViewModel(),
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