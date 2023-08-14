package org.don.bottomappbar.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation


private const val SETTINGS_GRAPH_ROUTE_PATTERN = "interests_graph"
const val interestsRoute = "interests_route"
fun NavController.navigateToSettingsGraph(navOptions: NavOptions? = null) {
    this.navigate(SETTINGS_GRAPH_ROUTE_PATTERN, navOptions)
}

fun NavGraphBuilder.interestsGraph(
    onTopicClick: (String) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = SETTINGS_GRAPH_ROUTE_PATTERN,
        startDestination = interestsRoute,
    ) {
        composable(route = interestsRoute) {
            //InterestsRoute(onTopicClick)
        }
        nestedGraphs()
    }
}
