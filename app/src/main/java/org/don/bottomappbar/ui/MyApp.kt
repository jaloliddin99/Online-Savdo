package org.don.bottomappbar.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import org.don.bottomappbar.navigation.NavigationDefaults
import org.don.bottomappbar.utils.gradientBackground
import org.don.bottomappbar.navigation.chatScreen
import org.don.bottomappbar.navigation.homeScreen
import org.don.bottomappbar.navigation.searchScreen
import org.don.bottomappbar.navigation.settingsScreen
import org.don.bottomappbar.ui.dialogs.settings.SettingsDialog
import org.don.bottomappbar.ui.theme.AppBackground
import org.don.bottomappbar.ui.theme.AppGradientBackground
import org.don.bottomappbar.ui.theme.GradientColors
import org.don.bottomappbar.ui.theme.LocalGradientColors
import org.don.bottomappbar.ui.theme.Purple90
import org.don.bottomappbar.ui.theme.PurpleGray90


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class
)
@Composable
fun MainScreenView(
    appState: ApplicationState = rememberNiaAppState()
) {
    val rememberNavController = appState.navController
    val currentBackStackEntry by rememberNavController.currentBackStackEntryAsState()
    val currentRoute by remember {
        derivedStateOf {
            currentBackStackEntry?.destination?.route ?: "home"
        }
    }

    var showSettingsDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showSettingsDialog) {
        SettingsDialog(
            onDismiss = { showSettingsDialog = false },
        )
    }

    val shouldShowGradientBackground =
        currentRoute == NavItems.Home.screenRoute

    AppBackground {
        AppGradientBackground(
            gradientColors = if (shouldShowGradientBackground) {
                LocalGradientColors.current
            } else {
                GradientColors()
            },
        ) {
            Scaffold(
                modifier = Modifier.semantics {
                    testTagsAsResourceId = true
                },
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = { BottomNavigation(rememberNavController, appState) }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .consumeWindowInsets(padding)
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Horizontal
                            )
                        )
                ) {
                    val destination = currentDestination(currentRoute)
                    if (destination != null) {
                        TopAppBar(
                            titleRes = destination.titleRes,
                            navigationIcon = Icons.Filled.Search,
                            navigationIconContentDescription = null,
                            actionIcon = Icons.Filled.Settings,
                            actionIconContentDescription = null,
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = Color.Transparent
                            ),
                            onActionClick = { showSettingsDialog = true },
                            onNavigationClick = {
                                appState.navigateToSearch()
                            }
                        )
                    }
                    NavigationGraph(appState)
                }
            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigation(
    navController: NavController,
    appState: ApplicationState
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        contentColor = NavigationDefaults.navigationContentColor(),
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            NavItems.Home,
            NavItems.Chat,
            NavItems.Settings
        )
        var selectedItemIndex by rememberSaveable {
            mutableIntStateOf(0)
        }
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEachIndexed { index, item ->

            NavigationBarItem(
                selected = currentRoute == item.screenRoute,
                alwaysShowLabel = true,
                onClick = {
                    appState.navigateToTopLevelDestination(item)
                },

                label = { Text(text = item.title) },

                icon = {
                    BadgedBox(badge = {
                        if (item.badgeCount != null) {
                            Badge {
                                Text(text = item.badgeCount.toString())
                            }
                        } else if (item.hasNews) {
                            Badge()
                        }
                    }) {
                        Icon(
                            imageVector = if (index == selectedItemIndex)
                                item.selectedIcon else item.unselectedIcon,

                            contentDescription = item.title
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NavigationDefaults.navigationSelectedItemColor(),
                    unselectedIconColor = NavigationDefaults.navigationContentColor(),
                    selectedTextColor = NavigationDefaults.navigationSelectedItemColor(),
                    unselectedTextColor = NavigationDefaults.navigationContentColor(),
                    indicatorColor = NavigationDefaults.navigationIndicatorColor(),
                )
            )
        }
    }
}

@Composable
fun NavigationGraph(appState: ApplicationState) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = NavItems.Home.screenRoute
    ) {

        homeScreen()
        chatScreen()
        settingsScreen()

        searchScreen(
            onBackClick = navController::popBackStack,
            onSettingsClick = { appState.navigateToTopLevelDestination(NavItems.Settings) },
        )
    }
}