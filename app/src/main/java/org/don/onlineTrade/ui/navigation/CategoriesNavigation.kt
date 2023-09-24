package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.don.onlineTrade.ui.add.ChatRoute
import org.don.onlineTrade.ui.categoriesList.CategoriesRoute

const val categoriesNavigationRoute = "categories"
fun NavController.navigateToCategories(navOptions: NavOptions? = null) {
    this.navigate(categoriesNavigationRoute, navOptions)
}

fun NavGraphBuilder.categoriesScreen() {
    composable(
        route = categoriesNavigationRoute,
    ) {
        CategoriesRoute()
    }
}