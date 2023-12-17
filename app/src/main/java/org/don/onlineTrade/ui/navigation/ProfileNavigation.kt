package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.don.onlineTrade.data.remote.models.category.CompactedCategoryItem
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
) {
    composable(route = profileNavigationRoute) { entry ->
        val item = entry.savedStateHandle.get<Boolean>("refresh_profile") ?: false
        if (item) {
            // Make your network call here
            // ...

            // Reset the flag to avoid repeated calls
            entry.savedStateHandle.set("refresh_profile", false)
        }
        ProfileRoute(
            toMyProducts = toMyProducts,
            toUpdateProfile = toUpdateProfile,
            refreshProfile = item
        )
    }
}


const val profileUpdateNavigationRoute = "profileUpdate"


fun NavGraphBuilder.profileUpdateScreen(goBackAndRefresh: () -> Unit) {
    composable(route = profileUpdateNavigationRoute) {
        UpdateProfileRoute(goBackAndRefresh = goBackAndRefresh)
    }
}