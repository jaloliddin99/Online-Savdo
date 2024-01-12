package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.don.onlineTrade.ui.profile.myPosts.MyPostsScreenRoute

const val myProductsNavigationRoute = "myProducts"


fun NavGraphBuilder.myProductsScreen(
    onItemClicked: (Int) -> Unit
) {
    composable(
        route = myProductsNavigationRoute,
    ) {
        MyPostsScreenRoute(
            onItemClicked = onItemClicked,
        )
    }
}