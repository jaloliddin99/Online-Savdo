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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Notifications
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
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import org.don.onlineTrade.data.remote.models.category.CategoryItem
import org.don.onlineTrade.data.remote.models.region.Data
import org.don.onlineTrade.ui.add.AddProductRoute
import org.don.onlineTrade.ui.auth.forgotPassword.ForgotPasswordRoute
import org.don.onlineTrade.ui.auth.forgotPassword.ResetPasswordRoute
import org.don.onlineTrade.ui.auth.login.SignInRoute
import org.don.onlineTrade.ui.auth.register.SignUpRoute
import org.don.onlineTrade.ui.auth.verify.VerificationRoute
import org.don.onlineTrade.ui.categoriesList.CategoriesRoute
import org.don.onlineTrade.ui.detailsPage.ProductDetailsRoute
import org.don.onlineTrade.ui.dialogs.settings.SettingsDialog
import org.don.onlineTrade.ui.dialogs.settings.UserEditableSettings
import org.don.onlineTrade.ui.filterCategory.FilterCategoryRoute
import org.don.onlineTrade.ui.home.HomeRoute
import org.don.onlineTrade.ui.home.search.SearchRoute
import org.don.onlineTrade.ui.map.MapScreenData
import org.don.onlineTrade.ui.map.MapShowLocationScreen
import org.don.onlineTrade.ui.map.MapsScreen
import org.don.onlineTrade.ui.navigation.NavigationDefaults
import org.don.onlineTrade.ui.navigation.Screen
import org.don.onlineTrade.ui.notification.NotificationsRoute
import org.don.onlineTrade.ui.profile.ProfileRoute
import org.don.onlineTrade.ui.profile.myPosts.MyPostsScreenRoute
import org.don.onlineTrade.ui.profile.update.UpdateProfileRoute
import org.don.onlineTrade.ui.profile.updatePassword.UpdatePasswordRoute
import org.don.onlineTrade.ui.region.RegionsRoute
import org.don.onlineTrade.ui.saved.SavedRoute
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
        rememberNavController.navigate(Screen.Notifications.route)
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
                        && destination.screenRoute != Screen.Categories.route
                        && destination.screenRoute != Screen.Regions.route
                        && destination.screenRoute != Screen.ProfileUpdate.route
                        && destination.screenRoute != Screen.PasswordUpdate.route
                        && destination.screenRoute != Screen.Notifications.route
                        && destination.screenRoute != Screen.MyProducts.route
                        && destination.screenRoute != Screen.ProductDetails.ROUTE
                        && destination.screenRoute != Screen.FilterCategory.ROUTE
                        && destination.screenRoute != Screen.AddProduct.route
                        && destination.screenRoute != Screen.Map.route
                        && destination.screenRoute != Screen.MapUserLocation.ROUTE
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
                        val showBackArrow = destination.screenRoute == Screen.Categories.route
                                || destination.screenRoute == Screen.Regions.route
                                || destination.screenRoute == Screen.ProfileUpdate.route
                                || destination.screenRoute == Screen.PasswordUpdate.route
                                || destination.screenRoute == Screen.ProductDetails.ROUTE
                                || destination.screenRoute == Screen.Notifications.route
                                || destination.screenRoute == Screen.MyProducts.route
                                || destination.screenRoute == Screen.FilterCategory.ROUTE
                                || destination.screenRoute == Screen.AddProduct.route
                                || destination.screenRoute == Screen.Map.route
                                || destination.screenRoute == Screen.MapUserLocation.ROUTE

                        TopAppBar(
                            title = stringResource(id = destination.titleRes),
                            navigationIcon = if (!showBackArrow) Icons.Filled.Search else Icons.Filled.ArrowBack,
                            navigationIconContentDescription = null,
                            actionIcon = if (destination.screenRoute == Screen.Profile.route) Icons.Filled.Settings else Icons.Outlined.Notifications,
                            actionIconContentDescription = null,
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = Color.Transparent
                            ),
                            onActionClick = {
                                if (destination.screenRoute == Screen.Profile.route) {
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
            Screen.Home.route
        } else {
            Screen.Welcome.route
        }
    ) {

        composable(route = Screen.Home.route) {
            HomeRoute(
                navigateToProduct = {
                    navController.navigate(Screen.ProductDetails(it).route)
                },
                navigateToCategory = {
                    navController.navigate(Screen.FilterCategory(it).route)
                }
            )
        }

        composable(
            route = Screen.ProductDetails.ROUTE,
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
                onSimilarItemClicked = {
                    navController.navigate(Screen.ProductDetails(it).route)
                },
                {},
                navController::popBackStack,
                goToMapsPage = { lat, long ->
                    navController.navigate(Screen.MapUserLocation(lat.toString(), long.toString()).route)
                }
            )
        }

        composable(
            route = Screen.FilterCategory.ROUTE,
            arguments = listOf(
                navArgument("param") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val param = backStackEntry.arguments?.getInt("param")
            FilterCategoryRoute(
                onItemClicked = {
                    navController.navigate(Screen.ProductDetails(it).route)
                },
                categoryId = param
            )
        }

        composable(route = Screen.AddProduct.route) { entry ->
            val item = entry.savedStateHandle.get<CategoryItem>("category_item")
            val map = entry.savedStateHandle.get<MapScreenData>("map_item")
            AddProductRoute(
                navigateToCategories = {
                    navController.navigate(Screen.Categories.route)
                },
                item = item,
                map = map,
                goToDetailsPage = {
                    navController.popBackStack()
                },
                goToMapScreen = {
                    navController.navigate(Screen.Map.route)
                }
            )
        }

        composable(route = Screen.Map.route) {
            MapsScreen(
                onBackClick = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("map_item", it)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.MapUserLocation.ROUTE,
            arguments = listOf(
                navArgument("latitude") {
                    type = NavType.StringType
                },
                navArgument("longitude") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val lat = (backStackEntry.arguments?.getString("latitude") ?: return@composable).toDouble()
            val long = (backStackEntry.arguments?.getString("longitude") ?: return@composable).toDouble()
            MapShowLocationScreen(
                lat = lat, lon = long
            )
        }

        composable(route = Screen.Notifications.route) {
            NotificationsRoute()
        }

        composable(route = Screen.Saved.route) {
            SavedRoute(
                navigateToProduct = {
                    navController.navigate(Screen.ProductDetails(it).route)
                }
            )
        }

        composable(route = Screen.Profile.route) { entry ->
            val item = entry.savedStateHandle.get<Boolean>("refresh_profile") ?: false
            if (item) {
                entry.savedStateHandle.set("refresh_profile", false)
            }
            ProfileRoute(
                toMyProducts = {
                    navController.navigate(Screen.MyProducts.route)
                },
                toUpdateProfile = {
                    navController.navigate(Screen.ProfileUpdate.route)
                },
                toUpdatePassword = {
                    navController.navigate(Screen.PasswordUpdate.route)
                },
                refreshProfile = item,
                toForgotPassword = {
                    navController.navigate(Screen.ForgotPassword(it).route)
                },
                goToRegistration = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                },
                restartApp = {
                    restartApp.invoke()
                }
            )
        }

        composable(route = Screen.ProfileUpdate.route) {
            UpdateProfileRoute(
                goBackAndRefresh = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("refresh_profile", true)
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.PasswordUpdate.route) {
            UpdatePasswordRoute(
                goBackAndRefresh = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("refresh_profile", true)
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.MyProducts.route) {
            MyPostsScreenRoute(
                onItemClicked = {
                    navController.navigate(Screen.ProductDetails(it).route)
                }
            )
        }

        composable(route = Screen.Search.route) {
            SearchRoute(
                onBackClick = navController::popBackStack,
                onItemClick = {
                    navController.navigate(Screen.ProductDetails(it).route)
                },
            )
        }

        composable(route = Screen.Welcome.route) {
            SignUpRoute(
                navigateToVerification = {
                    navController.navigate(Screen.Verification(it).route)
                },
                onLoginPage = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(route = Screen.Login.route) {
            SignInRoute(
                navigateToVerification = {
                    navController.navigate(Screen.Verification(it).route)
                },
                forgotPassword = {
                    navController.navigate(Screen.ForgotPassword(true).route)
                }
            )
        }

        composable(
            route = Screen.Verification.ROUTE,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val emailParam = backStackEntry.arguments?.getString("email")
            emailParam?.let {
                VerificationRoute(
                    emailParam = it,
                    navigateToMainScreen = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    },
                    onBackPressed = navController::popBackStack
                )
            }
        }

        composable(
            route = Screen.ForgotPassword.ROUTE,
            arguments = listOf(
                navArgument("fromLoginPage") {
                    type = NavType.BoolType
                }
            )
        ) {
            val fromLoginPage = it.arguments?.getBoolean("fromLoginPage") ?: true
            ForgotPasswordRoute(
                goToResetPage = { email ->
                    navController.navigate(Screen.ResetPassword(email, fromLoginPage).route)
                },
            )
        }

        composable(
            route = Screen.ResetPassword.ROUTE,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                },
                navArgument("fromLoginPage") {
                    type = NavType.BoolType
                }
            )
        ) {
            val email = it.arguments?.getString("email") ?: return@composable
            val fromLoginPage = it.arguments?.getBoolean("fromLoginPage") ?: true

            ResetPasswordRoute(
                goToLoginPage = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                },
                mEmail = email,
                fromLoginPage = fromLoginPage,
                onBackPressed = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(route = Screen.Categories.route) {
            CategoriesRoute(
                onBackPressed = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("category_item", it)
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Regions.route) {
            RegionsRoute(
                onRegionSelected = {
                    navController.navigate("district/${it.id}/${it.name}")
                }
            )
        }
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
