package uz.promo.selling.ui

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
import androidx.compose.ui.res.stringResource
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
import androidx.navigation.navDeepLink
import uz.promo.selling.data.remote.models.category.CategoryItem
import uz.promo.selling.ui.auth.forgotPassword.ForgotPasswordRoute
import uz.promo.selling.ui.auth.forgotPassword.ResetPasswordRoute
import uz.promo.selling.ui.auth.google.CompleteProfileScreen
import uz.promo.selling.ui.auth.google.GoogleAuthViewModel
import uz.promo.selling.ui.auth.login.SignInRoute
import uz.promo.selling.ui.auth.register.SignUpRoute
import uz.promo.selling.ui.auth.verify.VerificationRoute
import uz.promo.selling.ui.categoriesList.CategoriesRoute
import uz.promo.selling.ui.chat.ChatDetailRoute
import uz.promo.selling.ui.chat.ChatListRoute
import uz.promo.selling.ui.detailsPage.ProductDetailsRoute
import uz.promo.selling.ui.dialogs.settings.SettingsDialog
import uz.promo.selling.ui.dialogs.settings.UserEditableSettings
import uz.promo.selling.ui.filterCategory.FilterCategoryRoute
import uz.promo.selling.ui.main.add.AddProductRoute
import uz.promo.selling.ui.main.home.HomeRoute
import uz.promo.selling.ui.main.home.HomeViewModel
import uz.promo.selling.ui.main.home.search.SearchRoute
import uz.promo.selling.ui.map.MapScreenData
import uz.promo.selling.ui.map.MapShowLocationScreen
import uz.promo.selling.ui.map.MapsScreen
import uz.promo.selling.ui.map.SelectRadiusMapScreen
import uz.promo.selling.ui.navigation.NavigationDefaults
import uz.promo.selling.ui.navigation.Screen
import uz.promo.selling.ui.notification.NotificationsRoute
import uz.promo.selling.ui.main.profile.ProfileRoute
import uz.promo.selling.ui.main.myPosts.MyPostsScreenRoute
import uz.promo.selling.ui.main.profile.update.UpdateProfileRoute
import uz.promo.selling.ui.main.profile.updatePassword.UpdatePasswordRoute
import uz.promo.selling.ui.main.saved.SavedRoute
import uz.promo.selling.ui.theme.AppBackground
import uz.promo.selling.ui.theme.AppGradientBackground
import uz.promo.selling.ui.theme.GradientColors
import uz.promo.selling.ui.theme.LocalGradientColors
import uz.promo.selling.utils.AuthEvent
import uz.promo.selling.utils.FreeLoading
import uz.promo.selling.utils.SharedPref

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
                label = { Text(text = stringResource(id = item.titleRes)) },
                icon = {
                    Icon(
                        imageVector = if (isSelected)
                            item.selectedIcon else item.unselectedIcon,
                        contentDescription = stringResource(id = item.titleRes)
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
                    navigateToCategory = { parentId ->
                        // Tapping a parent category opens Search with all of its
                        // child categories preselected (like ticking the parent in
                        // the category picker) and runs the search.
                        navController.navigate(Screen.Search.route)
                        navController.getBackStackEntry(Screen.Search.route)
                            .savedStateHandle["preselect_parent_category_id"] = parentId
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
                    onMessagesClick = {
                        if (isUserLoggedIn()) {
                            navController.navigate(Screen.Chat.route)
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
                ),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "selling://open/post/{param}" },
                    navDeepLink { uriPattern = "https://selling.uz/post/{param}" },
                    navDeepLink { uriPattern = "https://selling.uz/{lang}/post/{param}" },
                    navDeepLink { uriPattern = "https://www.selling.uz/post/{param}" },
                    navDeepLink { uriPattern = "https://www.selling.uz/{lang}/post/{param}" },
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
                    },
                    navigateToChat = {
                        navController.navigate(Screen.ChatDetail(it).route)
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

            composable(
                route = Screen.Notifications.route,
                deepLinks = listOf(
                    navDeepLink { uriPattern = "selling://open/notifications" },
                )
            ) {
                NotificationsRoute(
                    onBackClick = navController::popBackStack,
                    onPostClick = {
                        navController.navigate(Screen.ProductDetails(it).route)
                    }
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
                    },
                    toNotificationSettings = {
                        navController.navigate(Screen.NotificationSettings.route)
                    },
                    toHelp = {
                        navController.navigate(Screen.Help.route)
                    }
                )
            }

            composable(route = Screen.Help.route) {
                uz.promo.selling.ui.main.profile.HelpRoute(
                    onBackClick = navController::popBackStack
                )
            }

            composable(route = Screen.NotificationSettings.route) {
                uz.promo.selling.ui.main.profile.NotificationSettingsRoute(
                    onBackClick = navController::popBackStack
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
                val preselectParentId =
                    entry.savedStateHandle.get<Int>("preselect_parent_category_id")
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
                    preselectParentCategoryId = preselectParentId,
                    searchBarModifier = searchBarModifier,
                )
            }

            composable(
                route = Screen.Welcome.route,
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) }
            ) {
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

                // Google sign-in failures were previously silent — surface them.
                LaunchedEffect(googleState.error) {
                    if (googleState.error.isNotBlank()) {
                        android.widget.Toast.makeText(
                            context, googleState.error, android.widget.Toast.LENGTH_LONG
                        ).show()
                    }
                }

                val brandingModifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "auth_branding"),
                    animatedVisibilityScope = this@composable
                )

                SignUpRoute(
                    navigateToVerification = {
                        navController.navigate(Screen.Verification(it).route)
                    },
                    onLoginPage = {
                        navController.navigate(Screen.Login.route)
                    },
                    onGoogleSignIn = { googleAuthVM.signInWithGoogle(context) },
                    brandingModifier = brandingModifier
                )

                // Visual feedback while the Google token exchange + profile check run,
                // so the screen isn't frozen-looking after the account picker closes.
                FreeLoading(googleState.isLoading, paddingTop = 64.dp)
            }

            composable(
                route = Screen.Login.route,
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) }
            ) {
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

                // Google sign-in failures were previously silent — surface them.
                LaunchedEffect(googleState.error) {
                    if (googleState.error.isNotBlank()) {
                        android.widget.Toast.makeText(
                            context, googleState.error, android.widget.Toast.LENGTH_LONG
                        ).show()
                    }
                }

                val brandingModifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "auth_branding"),
                    animatedVisibilityScope = this@composable
                )

                SignInRoute(
                    navigateToVerification = {
                        navController.navigate(Screen.Verification(it).route)
                    },
                    forgotPassword = {
                        navController.navigate(Screen.ForgotPassword(true).route)
                    },
                    onSignUpPage = {
                        // Sign-up lives on the Welcome screen; go back to it if
                        // it's on the stack, otherwise open it fresh.
                        if (!navController.popBackStack(Screen.Welcome.route, false)) {
                            navController.navigate(Screen.Welcome.route)
                        }
                    },
                    onGoogleSignIn = { googleAuthVM.signInWithGoogle(context) },
                    brandingModifier = brandingModifier
                )

                // Visual feedback while the Google token exchange + profile check run,
                // so the screen isn't frozen-looking after the account picker closes.
                FreeLoading(googleState.isLoading, paddingTop = 64.dp)
            }

            composable(route = Screen.CompleteProfile.route) {
                val googleAuthVM = hiltViewModel<GoogleAuthViewModel>()
                val googleState = googleAuthVM.state.value

                CompleteProfileScreen(
                    isLoading = googleState.isLoading,
                    onSubmit = { phone ->
                        googleAuthVM.completeProfile(phone) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(
                route = Screen.Verification.ROUTE,
                arguments = listOf(
                    navArgument("email") {
                        type = NavType.StringType
                    }
                ),
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) }
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

            composable(route = Screen.Chat.route) {
                ChatListRoute(
                    onBackClick = navController::popBackStack,
                    onConversationClick = {
                        navController.navigate(Screen.ChatDetail(it).route)
                    }
                )
            }

            composable(
                route = Screen.ChatDetail.ROUTE,
                arguments = listOf(
                    navArgument("conversationId") {
                        type = NavType.LongType
                    }
                ),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "selling://open/chat/{conversationId}" },
                )
            ) { backStackEntry ->
                val conversationId = backStackEntry.arguments?.getLong("conversationId")
                    ?: return@composable
                ChatDetailRoute(
                    conversationId = conversationId,
                    navigateBack = navController::popBackStack
                )
            }

        }
    }
}


fun isUserLoggedIn(): Boolean {
    return SharedPref.deviceToken.isNotEmpty() &&
            System.currentTimeMillis() < (SharedPref.loginTime + 1000L * 60 * 60 * 24 * 30)
}
