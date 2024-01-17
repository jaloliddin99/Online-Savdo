package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.don.onlineTrade.data.remote.models.category.CategoryItem
import org.don.onlineTrade.ui.add.AddProductRoute
import org.don.onlineTrade.ui.add.AddProductScreenViewModel

const val chatNavigationRoute = "add"
fun NavController.navigateToPosts(navOptions: NavOptions? = null) {
    this.navigate(chatNavigationRoute, navOptions)


}

fun NavGraphBuilder.addProductScreen(
    navigateToCategories: () -> Unit,
    goToDetailsPage: () -> Unit
) {
    composable(
        route = chatNavigationRoute
    ) { entry ->
        val item = entry.savedStateHandle.get<CategoryItem>("category_item")
        AddProductRoute(
            navigateToCategories = navigateToCategories,
            item = item,
            goToDetailsPage = goToDetailsPage
        )
    }
}