package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.don.onlineTrade.data.remote.models.category.CompactedCategoryItem
import org.don.onlineTrade.ui.add.ChatRoute

const val chatNavigationRoute = "chat"
fun NavController.navigateToChat(navOptions: NavOptions? = null) {
    this.navigate(chatNavigationRoute, navOptions)
}

fun NavGraphBuilder.chatScreen(
    navigateToCategories: () -> Unit,
    navigateToSelectRegions: () -> Unit,

) {
    composable(
        route = chatNavigationRoute,
    ) { entry ->
        val item = entry.savedStateHandle.get<CompactedCategoryItem>("category_item")
        ChatRoute(
            navigateToCategories = navigateToCategories,
            navigateToSelectRegions = navigateToSelectRegions,
            item = item
        )
    }
}