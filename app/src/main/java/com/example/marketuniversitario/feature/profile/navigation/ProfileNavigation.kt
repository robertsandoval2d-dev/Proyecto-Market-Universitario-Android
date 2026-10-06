package com.example.marketuniversitario.feature.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.marketuniversitario.feature.profile.ui.ProfileRoute
import com.example.marketuniversitario.feature.profile.ui.edit_profile.EditProfileRoute
import com.example.marketuniversitario.feature.profile.ui.preferences.PreferencesRoute
import com.example.marketuniversitario.feature.profile.ui.terms.TermsRoute

fun NavGraphBuilder.profileGraph(
    navController: NavHostController,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToPreferences: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    composable("profile_tab") {
        ProfileRoute(
            onNavigateToEditProfile = onNavigateToEditProfile,
            onNavigateToPreferences = onNavigateToPreferences,
            onNavigateToTerms = onNavigateToTerms,
            onLogoutSuccess = onLogoutSuccess
        )
    }
    composable("edit_profile") {
        EditProfileRoute(
            onNavigateBack = { navController.navigateUp() }
        )
    }
    composable("preferences") {
        PreferencesRoute(
            onNavigateBack = { navController.navigateUp() }
        )
    }
    composable("terms") {
        TermsRoute(
            onNavigateBack = { navController.navigateUp() }
        )
    }
}