package org.don.onlineTrade.ui

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import org.don.onlineTrade.ui.add.AddProductScreenViewModel
import org.don.onlineTrade.ui.navigation.NavigationDefaults
import org.don.onlineTrade.ui.navigation.addProductScreen
import org.don.onlineTrade.ui.navigation.homeScreen
import org.don.onlineTrade.ui.navigation.loginScreen
import org.don.onlineTrade.ui.navigation.searchScreen
import org.don.onlineTrade.ui.navigation.savedScreen
import org.don.onlineTrade.ui.navigation.welcomeScreen
import org.don.onlineTrade.ui.navigation.registrationScreen
import org.don.onlineTrade.ui.dialogs.settings.SettingsDialog
import org.don.onlineTrade.ui.dialogs.settings.UserEditableSettings
import org.don.onlineTrade.ui.navigation.categoriesNavigationRoute
import org.don.onlineTrade.ui.navigation.categoriesScreen
import org.don.onlineTrade.ui.navigation.districtsNavigationRoute
import org.don.onlineTrade.ui.navigation.districtsScreen
import org.don.onlineTrade.ui.navigation.filterCategoryNavigationRoute
import org.don.onlineTrade.ui.navigation.filterCategoryScreen
import org.don.onlineTrade.ui.navigation.forgotPasswordRoute
import org.don.onlineTrade.ui.navigation.myProductsNavigationRoute
import org.don.onlineTrade.ui.navigation.myProductsScreen
import org.don.onlineTrade.ui.navigation.navigateToPresent
import org.don.onlineTrade.ui.navigation.notificationsNavigationRoute
import org.don.onlineTrade.ui.navigation.notificationsScreen
import org.don.onlineTrade.ui.navigation.pDetailsNavigationRoute
import org.don.onlineTrade.ui.navigation.passwordUpdateNavigationRoute
import org.don.onlineTrade.ui.navigation.passwordUpdateScreen
import org.don.onlineTrade.ui.navigation.productDetailsScreen
import org.don.onlineTrade.ui.navigation.profileNavigationRoute
import org.don.onlineTrade.ui.navigation.profileScreen
import org.don.onlineTrade.ui.navigation.profileUpdateNavigationRoute
import org.don.onlineTrade.ui.navigation.profileUpdateScreen
import org.don.onlineTrade.ui.navigation.regionsNavigationRoute
import org.don.onlineTrade.ui.navigation.regionsScreen
import org.don.onlineTrade.ui.navigation.resetPasswordRoute
import org.don.onlineTrade.ui.navigation.verificationScreen
import org.don.onlineTrade.ui.theme.AppBackground
import org.don.onlineTrade.ui.theme.AppGradientBackground
import org.don.onlineTrade.ui.theme.GradientColors
import org.don.onlineTrade.ui.theme.LocalGradientColors
import org.don.onlineTrade.utils.SharedPref


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class
)
@Composable
fun MainScreenView(
    state: UserEditableSettings,
    appState: ApplicationState = rememberNiaAppState(),
    restartApp: () -> Unit

) {

    val rememberNavController = appState.navController
    val currentBackStackEntry by rememberNavController.currentBackStackEntryAsState()
    val currentRoute by remember {
        derivedStateOf {
            currentBackStackEntry?.destination?.route ?: "home"
        }
    }
    var toNotificationPage by rememberSaveable {
        mutableStateOf(false)
    }

    var showSettingsDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (toNotificationPage) {
        rememberNavController.navigate(notificationsNavigationRoute)
        toNotificationPage = false
    }

    if (showSettingsDialog) {
        SettingsDialog(
            state = state,
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
            val destination = appState.currentTopLevelDestination


            Scaffold(
                modifier = Modifier.semantics {
                    testTagsAsResourceId = true
                },
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    if (destination != null
                        && destination.screenRoute != categoriesNavigationRoute
                        && destination.screenRoute != regionsNavigationRoute
                        && destination.screenRoute != districtsNavigationRoute
                        && destination.screenRoute != profileUpdateNavigationRoute
                        && destination.screenRoute != passwordUpdateNavigationRoute
                        && destination.screenRoute != notificationsNavigationRoute
                        && destination.screenRoute != myProductsNavigationRoute
                        && destination.screenRoute != pDetailsNavigationRoute
                        && destination.screenRoute != filterCategoryNavigationRoute
                        && destination.screenRoute != NavItems.AddProduct.screenRoute
                        && destination.screenRoute != NavItems.MapScreen.screenRoute
                    ) {
                        BottomNavigation(rememberNavController, appState)
                    }
                }
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
                    if (destination != null) {
                        val showBackArrow = destination.screenRoute == categoriesNavigationRoute
                                || destination.screenRoute == regionsNavigationRoute
                                || destination.screenRoute == districtsNavigationRoute
                                || destination.screenRoute == profileUpdateNavigationRoute
                                || destination.screenRoute == passwordUpdateNavigationRoute
                                || destination.screenRoute == pDetailsNavigationRoute
                                || destination.screenRoute == notificationsNavigationRoute
                                || destination.screenRoute == myProductsNavigationRoute
                                || destination.screenRoute == filterCategoryNavigationRoute
                                || destination.screenRoute == NavItems.AddProduct.screenRoute
                                || destination.screenRoute == mapNavigationRoute

                        TopAppBar(
                            title = stringResource(id = destination.titleRes),
                            navigationIcon = if (!showBackArrow) Icons.Filled.Search else Icons.Filled.ArrowBack,
                            navigationIconContentDescription = null,
                            actionIcon = if (destination.screenRoute == profileNavigationRoute) Icons.Filled.Settings else Icons.Filled.Notifications,
                            actionIconContentDescription = null,
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = Color.Transparent
                            ),
                            onActionClick = {
                                if (destination.screenRoute == profileNavigationRoute) {
                                    showSettingsDialog = true
                                } else {
                                    toNotificationPage = true
                                }

                            },
                            onNavigationClick = {
                                if (showBackArrow) {
                                    toNotificationPage = false
                                    appState.navController.popBackStack()
                                } else {
                                    appState.navigateToSearch()
                                }
                            }
                        )
                    }
                    NavigationGraph(appState, restartApp )
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
            NavItems.AddProduct,
            NavItems.Saved,
            NavItems.Profile
        )
        val selectedItemIndex by rememberSaveable {
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
//                        if (item.badgeCount != null) {
//                            Badge {
//                                Text(text = item.badgeCount.toString())
//                            }
//                        } else if (item.hasNews) {
//                            //Badge()
//                        }
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
fun NavigationGraph(
    appState: ApplicationState,
    restartApp: () -> Unit
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = if (isUserHasRightToAccessToMainPart()) {
            NavItems.Home.screenRoute
        } else {
            welcomeScreen
        }
    ) {

        homeScreen(
            navigateToCategory = {
                navController.navigate("filterCategory/$it")
            },
            navigateToProduct = {
                navController.navigate("productDetails/$it")
            }
        )
        productDetailsScreen(
            onSimilarItemClicked = {
                navController.navigate("productDetails/$it")
            },
            onEditClicked = {},
            navigateBack = navController::popBackStack
        )
        filterCategoryScreen(
            onItemClicked = {
                navController.navigate("productDetails/$it")
            }
        )

        addProductScreen(
            navigateToCategories = {
                navController.navigate(categoriesNavigationRoute)
            },
            goToDetailsPage = {
                navController.popBackStack()
            },
            goToMapScreen = {
                navController.navigate(mapNavigationRoute)
            }
        )

        mapScreen(
            onBackClick = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("map_item", it)
                navController.popBackStack()
            }
        )

        notificationsScreen()
        savedScreen(
            navigateToProduct = {
                navController.navigate("productDetails/$it")
            }
        )
        profileScreen(
            toMyProducts = {
                navController.navigate(myProductsNavigationRoute)
            },
            toUpdateProfile = {
                navController.navigate(profileUpdateNavigationRoute)
            },
            toUpdatePassword = {
                navController.navigate(passwordUpdateNavigationRoute)
            },
            toForgotPassword = {
                navController.navigate("forgotPasswordRoute/$it")
            },
            goToRegistration = {
                navController.navigate(welcomeScreen) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            },
            restartApp = {
                restartApp.invoke()
            }
        )

        profileUpdateScreen(
            goBackAndRefresh = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("refresh_profile", true)
                navController.popBackStack()
            }
        )

        passwordUpdateScreen(
            goBackAndRefresh = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("refresh_profile", true)
                navController.popBackStack()
            }
        )

        myProductsScreen(
            onItemClicked = {
                navController.navigate("productDetails/$it")
            }
        )

        searchScreen(
            onBackClick = navController::popBackStack,
            onItemClick = {
                navController.navigate("productDetails/$it")
            },
        )

        registrationScreen(
            navigateToVerification = {
                navController.navigate("verification_screen/$it")
            },
            onLoginPage = {
                navController.navigate(loginScreen)
            }
        )

        loginScreen(
            navigationToVerification = {
                navController.navigate("verification_screen/$it")
            },
            forgotPassword = {
                navController.navigate("forgotPasswordRoute/${true}")
            }
        )

        verificationScreen(
            navigateToMainScreen = {
                navController.navigate(NavItems.Home.screenRoute) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            },
            onBackPressed = navController::popBackStack
        )

        forgotPasswordRoute { email: String, fromLogin: Boolean ->
            navController.navigate("resetPasswordRoute/${email}/$fromLogin")
        }

        resetPasswordRoute(
            navigateToLoginPage = {
                navController.navigate(loginScreen) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            },
            onBackPressed = {
                navController.navigate(NavItems.Profile.screenRoute) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            }
        )

        categoriesScreen(
            onBackPressed = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("category_item", it)

                navController.popBackStack()
            }

        )
        regionsScreen(
            onBackPressed = {
                navController.navigate("district/${it.id}/${it.name}")
            }
        )
        districtsScreen(
            onBackPressed = { district, region, lat, lon ->
                navController.navigate("add/${region.id}/${region.name}/${district.id}/${district.name}/$lat/$lon") {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            }
        )
    }
}


fun isUserHasRightToAccessToMainPart(
): Boolean {
    return if (SharedPref.deviceToken.isNotEmpty()){
        System.currentTimeMillis() < (SharedPref.loginTime  + 1000L *60*60*24*30)
    }else{
        false
    }
}