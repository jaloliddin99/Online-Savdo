package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.don.onlineTrade.data.remote.models.category.CompactedCategoryItem
import org.don.onlineTrade.data.remote.models.region.RegionDistrictModelItem
import org.don.onlineTrade.ui.add.AddProductRoute

const val chatNavigationRoute = "chat"
fun NavController.navigateToChat(navOptions: NavOptions? = null) {
    this.navigate(chatNavigationRoute, navOptions)
}

fun NavGraphBuilder.addProductScreen(
    navigateToCategories: () -> Unit,
    navigateToSelectRegions: () -> Unit,

) {
    composable(
        route = chatNavigationRoute,
    ) { entry ->
        val item = entry.savedStateHandle.get<CompactedCategoryItem>("category_item")
        val regions = entry.savedStateHandle.get<RegionDistrictModelItem>("regions_item")
        AddProductRoute(
            navigateToCategories = navigateToCategories,
            navigateToSelectRegions = navigateToSelectRegions,
            item = item,
            regions = regions
        )
    }
}