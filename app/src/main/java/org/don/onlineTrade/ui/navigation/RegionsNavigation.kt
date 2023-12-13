package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.don.onlineTrade.data.remote.models.region.Data
import org.don.onlineTrade.ui.region.RegionsRoute

const val regionsNavigationRoute = "regions"
fun NavController.navigateToRegions(navOptions: NavOptions? = null) {
    this.navigate(regionsNavigationRoute, navOptions)
}

fun NavGraphBuilder.regionsScreen(
    onBackPressed: (Data) -> Unit
) {
    composable(
        route = regionsNavigationRoute,
    ) {
        RegionsRoute(
            onRegionSelected = onBackPressed
        )
    }
}