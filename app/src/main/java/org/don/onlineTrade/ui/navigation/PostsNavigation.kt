package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.don.onlineTrade.data.remote.models.category.CategoryItem
import org.don.onlineTrade.ui.map.MapScreenData
import org.don.onlineTrade.ui.add.AddProductRoute

const val addNavigationRoute = "add"
fun NavController.navigateToPosts(navOptions: NavOptions? = null) {
    this.navigate(addNavigationRoute, navOptions)


}

fun NavGraphBuilder.addProductScreen(
    navigateToCategories: () -> Unit,
    goToDetailsPage: () -> Unit,
    goToMapScreen: () -> Unit
) {
    composable(
        route = addNavigationRoute
    ) { entry ->
        val item = entry.savedStateHandle.get<CategoryItem>("category_item")
        val map = entry.savedStateHandle.get<MapScreenData>("map_item")
        AddProductRoute(
            navigateToCategories = navigateToCategories,
            item = item,
            map = map,
            goToDetailsPage = goToDetailsPage,
            goToMapScreen = goToMapScreen
        )
    }
}