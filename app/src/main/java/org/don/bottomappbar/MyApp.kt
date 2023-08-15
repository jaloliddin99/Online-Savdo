package org.don.bottomappbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import currentDestination
import org.don.bottomappbar.navigation.searchScreen
import org.don.bottomappbar.ui.theme.Purple90
import org.don.bottomappbar.ui.theme.PurpleGray90


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun MainScreenView() {
    val rememberNavController = rememberNavController()
    val currentBackStackEntry by rememberNavController.currentBackStackEntryAsState()
    val currentRoute by remember {
        derivedStateOf {
            currentBackStackEntry?.destination?.route ?: "home"
        }
    }

    var showSettingsDialog by rememberSaveable {
        mutableStateOf(false)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .gradientBackground(
                colors =
                if (currentRoute == NavItems.Home.screenRoute)
                    listOf(PurpleGray90, Purple90)
                else
                    listOf(Color.White, Color.White),
                angle = -90f
            ),
        contentColor = Color.Transparent,
        color = Color.Transparent
    ) {
        Scaffold(
            bottomBar = { BottomNavigation(rememberNavController) },
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
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

                        }
                    )
                }
                NavigationGraph(rememberNavController)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigation(navController: NavController) {
    NavigationBar {
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
                    navController.navigate(item.screenRoute) {
                        navController.graph.startDestinationRoute?.let { screenRoute ->
                            popUpTo(screenRoute) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    selectedItemIndex = index
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
                }
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavItems.Home.screenRoute) {
        composable(NavItems.Home.screenRoute) {
            BottomNavContentScreens.HomeScreen()
        }
        composable(NavItems.Chat.screenRoute) {
            BottomNavContentScreens.ChatScreen()
        }
        composable(NavItems.Settings.screenRoute) {
            BottomNavContentScreens.SettingsScreen()
        }

        searchScreen(
            onBackClick = navController::popBackStack,
            onSettingsClick = { appState.navigateToTopLevelDestination(INTERESTS) },
            onTopicClick = navController::navigateToTopic,
        )
    }
}