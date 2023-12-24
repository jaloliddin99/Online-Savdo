package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.don.onlineTrade.ui.profile.ProfileRoute
import org.don.onlineTrade.ui.profile.update.UpdateProfileRoute
import org.don.onlineTrade.ui.profile.updatePassword.UpdatePasswordRoute

const val profileNavigationRoute = "profile"

fun NavController.navigateToProfile(navOptions: NavOptions? = null) {
    this.navigate(profileNavigationRoute, navOptions)
}

fun NavGraphBuilder.profileScreen(
    toMyProducts: () -> Unit,
    toUpdateProfile: () -> Unit,
    toUpdatePassword: () -> Unit,
    toForgotPassword: (Boolean) -> Unit,
    goToRegistration: () -> Unit,
    restartApp: () -> Unit
) {
    composable(route = profileNavigationRoute) { entry ->
        val item = entry.savedStateHandle.get<Boolean>("refresh_profile") ?: false
        if (item) {
            entry.savedStateHandle.set("refresh_profile", false)
        }
        ProfileRoute(
            toMyProducts = toMyProducts,
            toUpdateProfile = toUpdateProfile,
            toUpdatePassword = toUpdatePassword,
            refreshProfile = item,
            toForgotPassword = toForgotPassword,
            goToRegistration = goToRegistration,
            restartApp = restartApp
        )
    }
}


const val profileUpdateNavigationRoute = "profileUpdate"


fun NavGraphBuilder.profileUpdateScreen(goBackAndRefresh: () -> Unit) {
    composable(route = profileUpdateNavigationRoute) {
        UpdateProfileRoute(goBackAndRefresh = goBackAndRefresh)
    }
}

const val passwordUpdateNavigationRoute = "passwordUpdate"

fun NavGraphBuilder.passwordUpdateScreen(goBackAndRefresh: () -> Unit) {
    composable(route = passwordUpdateNavigationRoute) {
        UpdatePasswordRoute(goBackAndRefresh = goBackAndRefresh)
    }
}