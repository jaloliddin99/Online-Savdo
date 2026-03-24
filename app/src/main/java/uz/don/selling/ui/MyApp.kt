package uz.don.selling.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import uz.don.selling.data.remote.models.category.CategoryItem
import uz.don.selling.ui.auth.forgotPassword.ForgotPasswordRoute
import uz.don.selling.ui.auth.forgotPassword.ResetPasswordRoute
import uz.don.selling.ui.auth.google.CompleteProfileScreen
import uz.don.selling.ui.auth.google.GoogleAuthViewModel
import uz.don.selling.ui.auth.login.SignInRoute
import uz.don.selling.ui.auth.register.SignUpRoute
import uz.don.selling.ui.auth.verify.VerificationRoute
import uz.don.selling.ui.categoriesList.CategoriesRoute
import uz.don.selling.ui.detailsPage.ProductDetailsRoute
import uz.don.selling.ui.dialogs.settings.SettingsDialog
import uz.don.selling.ui.dialogs.settings.UserEditableSettings
import uz.don.selling.ui.filterCategory.FilterCategoryRoute
import uz.don.selling.ui.main.add.AddProductRoute
import uz.don.selling.ui.main.home.HomeRoute
import uz.don.selling.ui.main.home.HomeViewModel
import uz.don.selling.ui.main.home.search.SearchRoute
import uz.don.selling.ui.map.MapScreenData
import uz.don.selling.ui.map.MapShowLocationScreen
import uz.don.selling.ui.map.MapsScreen
import uz.don.selling.ui.map.SelectRadiusMapScreen
import uz.don.selling.ui.navigation.NavigationDefaults
import uz.don.selling.ui.navigation.Screen
import uz.don.selling.ui.notification.NotificationsRoute
import uz.don.selling.ui.main.profile.ProfileRoute
import uz.don.selling.ui.main.myPosts.MyPostsScreenRoute
import uz.don.selling.ui.main.profile.update.UpdateProfileRoute
import uz.don.selling.ui.main.profile.updatePassword.UpdatePasswordRoute
import uz.don.selling.ui.main.saved.SavedRoute
import uz.don.selling.ui.theme.AppBackground
import uz.don.selling.ui.theme.AppGradientBackground
import uz.don.selling.ui.theme.GradientColors
import uz.don.selling.ui.theme.LocalGradientColors
import uz.don.selling.utils.AuthEvent
import uz.don.selling.utils.SharedPref

private val BOTTOM_BAR_ROUTES = setOf(
    Screen.Home.route,
    Screen.MyProducts.route,
    Screen.AddProduct.route,
    Screen.Saved.route,
    Screen.Profile.route
)

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
    val currentRoute = appState.currentDestination?.route
    val showBottomBar = currentRoute in BOTTOM_BAR_ROUTES

    LaunchedEffect(Unit) {
        AuthEvent.unauthorizedFlow.collect {
            if (SharedPref.deviceToken.isNotEmpty()) {
                SharedPref.deviceToken = ""
                SharedPref.refreshToken = ""
                appState.navController.navigate(Screen.Welcome.route) {
                    popUpTo(appState.navController.graph.id) {
                        inclusive = true
                    }
                }
            }
        }
    }

    var showSettingsDialog by rememberSaveable {
        mutableStateOf(false)
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
            Scaffold(
                modifier = Modifier.semantics {
                    testTagsAsResourceId = true
                },
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    AnimatedVisibility(
                        visible = showBottomBar,
                        enter = slideInVertically { it },
                        exit = slideOutVertically { it }
                    ) {
                        BottomNavigation(appState.navController, appState)
                    }
                }
            ) { padding ->
                Box(
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
                    NavigationGraph(
                        appState = appState,
                        restartApp = restartApp,
                        onSettingsClick = { showSettingsDialog = true }
                    )
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
            NavItems.MyPosts,
            NavItems.AddProduct,
            NavItems.Saved,
            NavItems.Profile
        )
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            val isSelected = currentRoute == item.screenRoute

            NavigationBarItem(
                selected = isSelected,
                alwaysShowLabel = true,
                onClick = {
                    appState.navigateToTopLevelDestination(item)
                },
                label = { Text(text = item.title) },
                icon = {
                    Icon(
                        imageVector = if (isSelected)
                            item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
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
@OptIn(ExperimentalSharedTransitionApi::class)
fun NavigationGraph(
    appState: ApplicationState,
    restartApp: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val navController = appState.navController
    val homeViewModel: HomeViewModel = hiltViewModel()

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {

            composable(route = Screen.Home.route) { entry ->
                val graphEntry = remember(entry) {
                    navController.getBackStackEntry(navController.graph.id)
                }
                val homeViewModel = hiltViewModel<HomeViewModel>(graphEntry)
                val searchBarModifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "search_bar"),
                    animatedVisibilityScope = this@composable
                )
                HomeRoute(
                    homeViewModel = homeViewModel,
                    navigateToProduct = {
                        navController.navigate(Screen.ProductDetails(it).route)
                    },
                    navigateToCategory = {
                        navController.navigate(Screen.FilterCategory(it).route)
                    },
                    onSearchClick = { appState.navigateToSearch() },
                    onMapClick = {
                        navController.navigate(Screen.Search.route)
                        navController.navigate(Screen.MapSearch.route)
                    },
                    onNotificationClick = {
                        if (isUserLoggedIn()) {
                            navController.navigate(Screen.Notifications.route)
                        } else {
                            navController.navigate(Screen.Welcome.route)
                        }
                    },
                    searchBarModifier = searchBarModifier,
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
                    productId = param ?: 0,
                    onSimilarItemClicked = {
                        navController.navigate(Screen.ProductDetails(it).route)
                    },
                    onEditClicked = {},
                    navigateBack = navController::popBackStack,
                    goToMapsPage = { lat, long ->
                        navController.navigate(
                            Screen.MapUserLocation(
                                lat.toString(),
                                long.toString()
                            ).route
                        )
                    },
                    onLoginRequired = {
                        navController.navigate(Screen.Welcome.route)
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
                    categoryId = param,
                    onBackClick = navController::popBackStack
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

            composable(route = Screen.MapSearch.route) {
                SelectRadiusMapScreen(
                    onBackClick = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("map_search_data", it)
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
                val lat = (backStackEntry.arguments?.getString("latitude")
                    ?: return@composable).toDouble()
                val long = (backStackEntry.arguments?.getString("longitude")
                    ?: return@composable).toDouble()
                MapShowLocationScreen(
                    lat = lat, lon = long
                )
            }

            composable(route = Screen.Notifications.route) {
                NotificationsRoute(
                    onBackClick = navController::popBackStack
                )
            }

            composable(
                route = Screen.Saved.route,
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) }
            ) {
                SavedRoute(
                    navigateToProduct = {
                        navController.navigate(Screen.ProductDetails(it).route)
                    }
                )
            }

            composable(
                route = Screen.Profile.route,
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) }
            ) { entry ->
                val item = entry.savedStateHandle.get<Boolean>("refresh_profile") ?: false
                if (item) {
                    entry.savedStateHandle.set("refresh_profile", false)
                }
                ProfileRoute(
                    onSettingsClick = onSettingsClick,
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
                    },
                    toNotifications = {
                        navController.navigate(Screen.Notifications.route)
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

            composable(
                route = Screen.Search.route,
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) }
            ) { entry ->
                val mapData = entry.savedStateHandle.get<MapScreenData>("map_search_data")
                val categoryItem = entry.savedStateHandle.get<CategoryItem>("category_item")
                val searchBarModifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "search_bar"),
                    animatedVisibilityScope = this@composable
                )
                SearchRoute(
                    onBackClick = navController::popBackStack,
                    onItemClick = {
                        navController.navigate(Screen.ProductDetails(it).route)
                    },
                    onMapClick = {
                        navController.navigate(Screen.MapSearch.route)
                    },
                    onCategoryClick = {
                        navController.navigate(Screen.Categories.route)
                    },
                    mapSearchData = mapData,
                    categoryItem = categoryItem,
                    searchBarModifier = searchBarModifier,
                )
            }

            composable(route = Screen.Welcome.route) {
                val googleAuthVM = hiltViewModel<GoogleAuthViewModel>()
                val googleState = googleAuthVM.state.value
                val context = androidx.compose.ui.platform.LocalContext.current

                LaunchedEffect(googleState.result) {
                    if (googleState.result != null && googleState.result.status) {
                        if (googleState.needsPhone) {
                            navController.navigate(Screen.CompleteProfile.route) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    }
                }

                SignUpRoute(
                    navigateToVerification = {
                        navController.navigate(Screen.Verification(it).route)
                    },
                    onLoginPage = {
                        navController.navigate(Screen.Login.route)
                    },
                    onGoogleSignIn = { googleAuthVM.signInWithGoogle(context) }
                )
            }

            composable(route = Screen.Login.route) {
                val googleAuthVM = hiltViewModel<GoogleAuthViewModel>()
                val googleState = googleAuthVM.state.value
                val context = androidx.compose.ui.platform.LocalContext.current

                LaunchedEffect(googleState.result) {
                    if (googleState.result != null && googleState.result.status) {
                        if (googleState.needsPhone) {
                            navController.navigate(Screen.CompleteProfile.route) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    }
                }

                SignInRoute(
                    navigateToVerification = {
                        navController.navigate(Screen.Verification(it).route)
                    },
                    forgotPassword = {
                        navController.navigate(Screen.ForgotPassword(true).route)
                    },
                    onGoogleSignIn = { googleAuthVM.signInWithGoogle(context) }
                )
            }

            composable(route = Screen.CompleteProfile.route) {
                val googleAuthVM = hiltViewModel<GoogleAuthViewModel>()
                val googleState = googleAuthVM.state.value

                LaunchedEffect(googleState.needsPhone) {
                    if (googleState.result != null && !googleState.needsPhone) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                }

                CompleteProfileScreen(
                    isLoading = googleState.isLoading,
                    onSubmit = { phone -> googleAuthVM.completeProfile(phone) }
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

            composable(route = Screen.Categories.route) { entry ->
                CategoriesRoute(
                    homeViewModel = homeViewModel,
                    onBackPressed = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("category_item", it)
                        navController.popBackStack()
                    },
                    onBackClick = navController::popBackStack
                )
            }

        }
    }
}


fun isUserLoggedIn(): Boolean {
    return SharedPref.deviceToken.isNotEmpty() &&
            System.currentTimeMillis() < (SharedPref.loginTime + 1000L * 60 * 60 * 24 * 30)
}
