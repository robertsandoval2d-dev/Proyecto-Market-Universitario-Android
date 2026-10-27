package com.example.marketuniversitario.feature.auth.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.marketuniversitario.feature.auth.ui.forgot_password.ForgotPasswordRoute
import com.example.marketuniversitario.feature.auth.ui.login.LoginRoute
import com.example.marketuniversitario.feature.auth.ui.sign_up.SignUpRoute
import com.example.marketuniversitario.feature.auth.ui.welcome.WelcomeRoute
import com.example.marketuniversitario.feature.user.ui.profile_completion.ProfileCompletionRoute

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
            onLoginSuccess = { isProfileComplete, isAdmin ->
                val destination = if (!isProfileComplete) "profile_completion"
                else if (isAdmin) "role_selection"
                else "inicio"
                navHostController.navigate(destination) {
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
            onNavigateHome = { isProfileComplete, isAdmin ->
                val destination = if (!isProfileComplete) "profile_completion"
                else if (isAdmin) "role_selection"
                else "inicio"
                navHostController.navigate(destination) {
                    popUpTo("welcome") { inclusive = true }
                }
            }
        )
    }

    composable("login") {
        LoginRoute(
            viewModel = hiltViewModel(),
            onLoginSuccess = { isProfileComplete, isAdmin ->
                val destination = if (!isProfileComplete) "profile_completion"
                else if (isAdmin) "role_selection"
                else "inicio"
                navHostController.navigate(destination) {
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

    composable("profile_completion") {
        ProfileCompletionRoute(
            onNavigateHome = { isAdmin ->
                val destination = if (isAdmin) "role_selection" else "inicio"
                navHostController.navigate(destination) {
                    popUpTo(navHostController.graph.id) { inclusive = true }
                }
            },
            onNavigateToWelcome = {
                navHostController.navigate("welcome") {
                    popUpTo(navHostController.graph.id) { inclusive = true }
                }
            }
        )
    }
}
