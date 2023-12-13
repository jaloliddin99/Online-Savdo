package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.don.onlineTrade.data.remote.models.region.Data
import org.don.onlineTrade.data.remote.models.region.DataDistrict
import org.don.onlineTrade.ui.region.RegionsRoute
import org.don.onlineTrade.ui.region.district.DistrictsRoute

const val districtsNavigationRoute = "district/{region_id}/{region_name}"
fun NavController.navigateToDistricts(navOptions: NavOptions? = null) {
    this.navigate(districtsNavigationRoute, navOptions)
}

fun NavGraphBuilder.districtsScreen(
    onBackPressed: (DataDistrict, Data) -> Unit
) {
    composable(
        route = districtsNavigationRoute,
        arguments = listOf(
            navArgument("region_name") {
                type = NavType.StringType
            },
            navArgument("region_id") {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->
        val regionId = backStackEntry.arguments?.getInt("region_id")
        val regionName = backStackEntry.arguments?.getString("region_name")

        if (regionName != null && regionId != null) {
            DistrictsRoute(
                onBackPressed = onBackPressed,
                region = Data(regionId, regionName)
            )

        }
    }
}