package com.example.marketuniversitario.feature.auth.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.marketuniversitario.feature.auth.ui.forgot_password.ForgotPasswordRoute
import com.example.marketuniversitario.feature.auth.ui.login.LoginRoute
import com.example.marketuniversitario.feature.auth.ui.sign_up.SignUpRoute
import com.example.marketuniversitario.feature.auth.ui.welcome.WelcomeRoute

fun NavGraphBuilder.authGraph(navHostController: NavHostController) {
    composable("welcome") {
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

    composable("signup") {
        SignUpRoute(
            viewModel = hiltViewModel(),
            onRegisterSuccess = {
                navHostController.navigate("login")
            },
            onNavigateBack = {
                navHostController.navigateUp()
            },
            onNavigateHome = {
                navHostController.navigate("inicio") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
        )
    }

    composable("login") {
        LoginRoute(
            viewModel = hiltViewModel(),
            onLoginSuccess = {
                navHostController.navigate("inicio") {
                    popUpTo("welcome") { inclusive = true }
                }
            },
            onNavigateToForgotPassword = {
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
}
