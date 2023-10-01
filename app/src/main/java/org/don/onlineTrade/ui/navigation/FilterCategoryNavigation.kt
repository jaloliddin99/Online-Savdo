package org.don.onlineTrade.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.paging.compose.collectAsLazyPagingItems
import org.don.onlineTrade.ui.detailsPage.ProductDetailsRoute
import org.don.onlineTrade.ui.filterCategory.FilterCategoryRoute
import org.don.onlineTrade.ui.home.HomeViewModel


const val filterCategoryNavigationRoute = "filterCategory/{param}"
fun NavController.navigateToFilter(navOptions: NavOptions? = null) {
    this.navigate(filterCategoryNavigationRoute, navOptions)
}

fun NavGraphBuilder.filterCategoryScreen(
    onItemClicked: (Int) -> Unit
) {
    composable(
        route = filterCategoryNavigationRoute,
        arguments = listOf(
            navArgument("param") {
                type = NavType.IntType
                defaultValue = 0
            }
        )
    ) { backStackEntry ->
        val param = backStackEntry.arguments?.getInt("param")
        val viewModel = hiltViewModel<HomeViewModel>()
        val pagingItems = viewModel.collectProducts(
            categoryId = param
        ).collectAsLazyPagingItems().itemSnapshotList.items
        FilterCategoryRoute(
            list = pagingItems,
            onItemClicked = onItemClicked
        )
    }
}
