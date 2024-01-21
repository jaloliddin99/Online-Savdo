package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.don.onlineTrade.ui.detailsPage.ProductDetailsRoute


const val pDetailsNavigationRoute = "productDetails/{param}"
fun NavGraphBuilder.productDetailsScreen(
    onSimilarItemClicked: (Int) -> Unit,
    onEditClicked: (Int) -> Unit,
    navigateBack: () -> Unit,
) {
    composable(
        route = pDetailsNavigationRoute,
        arguments = listOf(
            navArgument("param") {
                type = NavType.IntType
                defaultValue = 0
            }
        )
    ) { backStackEntry ->
        val param = backStackEntry.arguments?.getInt("param")
        ProductDetailsRoute(
            param ?: 0,
            onSimilarItemClicked = onSimilarItemClicked,
            onEditClicked,
            navigateBack,
        )
    }
}
