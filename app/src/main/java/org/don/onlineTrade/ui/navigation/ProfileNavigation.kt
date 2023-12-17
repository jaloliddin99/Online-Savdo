package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.don.onlineTrade.ui.dialogs.settings.UserEditableSettings
import org.don.onlineTrade.ui.profile.ProfileRoute
import org.don.onlineTrade.ui.profile.update.UpdateProfileRoute

const val profileNavigationRoute = "profile"

fun NavController.navigateToProfile(navOptions: NavOptions? = null) {
    this.navigate(profileNavigationRoute, navOptions)
}

fun NavGraphBuilder.profileScreen(
    toMyProducts: () -> Unit,
    toUpdateProfile: () -> Unit,
    state: UserEditableSettings
) {
    composable(route = profileNavigationRoute) {
        ProfileRoute(
            toMyProducts = toMyProducts,
            toUpdateProfile = toUpdateProfile
        )
    }
}


const val profileUpdateNavigationRoute = "profileUpdate"


fun NavGraphBuilder.profileUpdateScreen(goBackAndRefresh: () -> Unit) {
    composable(route = profileUpdateNavigationRoute) {
        UpdateProfileRoute(goBackAndRefresh = goBackAndRefresh)
    }
}