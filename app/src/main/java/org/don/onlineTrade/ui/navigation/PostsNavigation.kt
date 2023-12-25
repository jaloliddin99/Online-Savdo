package org.don.onlineTrade.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.don.onlineTrade.data.remote.models.category.CompactedCategoryItem
import org.don.onlineTrade.ui.add.AddProductRoute
import org.don.onlineTrade.ui.add.AddProductScreenViewModel

const val chatNavigationRoute = "add/{reg_id}/{reg_name}/{dis_id}/{dis_name}"
fun NavController.navigateToPosts(navOptions: NavOptions? = null) {
    this.navigate("add/${-1}/${"e"}/${-1}/${"e"}", navOptions)


}

fun NavGraphBuilder.addProductScreen(
    navigateToCategories: () -> Unit,
    navigateToSelectRegions: () -> Unit,
    addProductViewModel: AddProductScreenViewModel,
    goToDetailsPage: (Int) -> Unit
) {
    composable(
        route = chatNavigationRoute,
        arguments = listOf(
            navArgument("reg_name") {
                type = NavType.StringType
            },
            navArgument("reg_id") {
                type = NavType.IntType
            },
            navArgument("dis_name") {
                type = NavType.StringType
            },
            navArgument("dis_id") {
                type = NavType.IntType
            }
        )
    ) { entry ->
        val reg_name = entry.arguments?.getString("reg_name")
        val dis_name = entry.arguments?.getString("dis_name")
        val reg_id = entry.arguments?.getInt("reg_id")
        val dis_id = entry.arguments?.getInt("dis_id")
        val item = entry.savedStateHandle.get<CompactedCategoryItem>("category_item")
        AddProductRoute(
            navigateToCategories = navigateToCategories,
            navigateToSelectRegions = navigateToSelectRegions,
            item = item,
            regName = reg_name,
            disName = dis_name,
            regId = reg_id,
            disId = dis_id,
            addProductViewModel = addProductViewModel,
            goToDetailsPage = goToDetailsPage
        )
    }
}