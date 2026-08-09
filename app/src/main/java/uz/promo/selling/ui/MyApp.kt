package uz.promo.selling.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
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
import uz.promo.selling.ui.chat.ChatUnreadViewModel
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
import uz.promo.selling.ui.navigation.Screen
import uz.promo.selling.ui.notification.NotificationsRoute
import uz.promo.selling.ui.main.profile.ProfileRoute
import uz.promo.selling.ui.main.myPosts.MyPostsScreenRoute
import uz.promo.selling.ui.main.profile.update.UpdateProfileRoute
import uz.promo.selling.ui.main.profile.updatePassword.UpdatePasswordRoute
import uz.promo.selling.ui.main.saved.SavedRoute
import uz.promo.selling.ui.main.saved.SavedPostsRoute
import uz.promo.selling.ui.theme.AppBackground
import uz.promo.selling.ui.theme.AppGradientBackground
import uz.promo.selling.ui.theme.GradientColors
import uz.promo.selling.ui.theme.LocalGradientColors
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.utils.AuthEvent
import uz.promo.selling.utils.FreeLoading
import uz.promo.selling.utils.SharedPref
import uz.promo.selling.utils.localizedError
import kotlin.math.roundToInt

private val BOTTOM_BAR_ROUTES = setOf(
    Screen.Home.route,
    Screen.SavedPosts.route,
    Screen.AddProduct.route,
    Screen.Chat.route,
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
    // The Add tab hides the bar once the user enters a create flow (AI or manual
    // wizard) so it doesn't cover the wizard's own bottom buttons.
    var addFlowActive by remember { mutableStateOf(false) }
    val showBottomBar = currentRoute in BOTTOM_BAR_ROUTES && !addFlowActive

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
            // Backdrop for the glass bottom bar — the content behind it gets blurred.
            val hazeState = remember { HazeState() }
            Scaffold(
                modifier = Modifier.semantics {
                    testTagsAsResourceId = true
                },
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize()) {
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
                            .hazeSource(state = hazeState)
                    ) {
                        NavigationGraph(
                            appState = appState,
                            restartApp = restartApp,
                            onSettingsClick = { showSettingsDialog = true },
                            onAddFlowActiveChange = { addFlowActive = it }
                        )
                    }
                    // The bar overlays the content (no reserved strip) so the area
                    // around the pill is fully transparent and content scrolls under it.
                    AnimatedVisibility(
                        visible = showBottomBar,
                        modifier = Modifier.align(Alignment.BottomCenter),
                        enter = slideInVertically { it },
                        exit = slideOutVertically { it }
                    ) {
                        BottomNavigation(appState.navController, appState, hazeState)
                    }
                }
            }
        }
    }
}


/**
 * Floating "liquid glass" tab bar (iOS 26 style): a translucent rounded pill that
 * hovers above the bottom edge, with a gooey selection indicator — its leading edge
 * rushes ahead on a stiff spring while the trailing edge catches up on a soft one,
 * so the pill stretches like liquid in transit and contracts on arrival.
 */
@Composable
fun BottomNavigation(
    navController: NavController,
    appState: ApplicationState,
    hazeState: HazeState
) {
    val items = listOf(
        NavItems.Home,
        NavItems.SavedPosts,
        NavItems.AddProduct,
        NavItems.Chat,
        NavItems.Profile
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Unread message count for the Chat tab badge; refresh as the user moves around.
    val chatUnreadVM: ChatUnreadViewModel = hiltViewModel()
    LaunchedEffect(currentRoute) { chatUnreadVM.refresh() }

    val selectedIndex = items.indexOfFirst { it.screenRoute == currentRoute }
    val currentSelected by rememberUpdatedState(selectedIndex)

    // While the user holds & drags, the pill follows the finger (in tab units);
    // on release it snaps to the nearest tab and navigates there.
    var dragPosition by remember { mutableStateOf<Float?>(null) }
    val pillTarget = dragPosition ?: selectedIndex.coerceAtLeast(0).toFloat()
    // The tab visuals highlight the tab the pill is hovering over, live during the drag.
    val highlightIndex = dragPosition?.roundToInt()?.coerceIn(0, items.size - 1) ?: selectedIndex

    val fastEdge by animateFloatAsState(
        targetValue = pillTarget,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 900f),
        label = "navFastEdge"
    )
    val slowEdge by animateFloatAsState(
        targetValue = pillTarget,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 260f),
        label = "navSlowEdge"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 10.dp)
    ) {
        val shape = RoundedCornerShape(28.dp)
        val surfaceColor = MaterialTheme.colorScheme.surface
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = shape,
                    ambientColor = Color.Black.copy(alpha = 0.22f),
                    spotColor = Color.Black.copy(alpha = 0.22f)
                )
                .clip(shape)
                // Real backdrop blur of the content scrolling underneath.
                .hazeEffect(state = hazeState) {
                    backgroundColor = surfaceColor
                    blurRadius = 24.dp
                    noiseFactor = 0f
                    tints = listOf(HazeTint(surfaceColor.copy(alpha = 0.45f)))
                    // Pre-Android-12 devices can't blur — use a stronger scrim instead.
                    fallbackTint = HazeTint(surfaceColor.copy(alpha = 0.88f))
                }
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.30f), Color.White.copy(alpha = 0.06f))
                    ),
                    shape = shape
                )
                // Hold & drag anywhere on the bar: the pill chases the finger and,
                // on release, snaps to the nearest tab and navigates. Taps still
                // work — the drag only kicks in past the touch slop.
                .pointerInput(items.size) {
                    val tabWidthPx = size.width.toFloat() / items.size
                    detectDragGestures(
                        onDragStart = { offset ->
                            dragPosition = (offset.x / tabWidthPx - 0.5f)
                                .coerceIn(0f, items.size - 1f)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            dragPosition = (change.position.x / tabWidthPx - 0.5f)
                                .coerceIn(0f, items.size - 1f)
                        },
                        onDragEnd = {
                            val target = dragPosition?.roundToInt()?.coerceIn(0, items.size - 1)
                            dragPosition = null
                            if (target != null && target != currentSelected) {
                                appState.navigateToTopLevelDestination(items[target])
                            }
                        },
                        onDragCancel = { dragPosition = null }
                    )
                }
        ) {
            val tabWidth = maxWidth / items.size

            // Liquid selection pill — slightly wider than its tab slot.
            if (selectedIndex >= 0 || dragPosition != null) {
                val left = minOf(fastEdge, slowEdge)
                val right = maxOf(fastEdge, slowEdge)
                val extra = 14.dp
                val offsetX = (tabWidth * left - extra / 2).coerceAtLeast(0.dp)
                val pillWidth = (tabWidth * (right - left + 1f) + extra)
                    .coerceAtMost(maxWidth - offsetX)
                Box(
                    modifier = Modifier
                        .offset(x = offsetX)
                        .width(pillWidth)
                        .fillMaxHeight()
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f))
                )
            }

            Row(modifier = Modifier.fillMaxSize()) {
                items.forEachIndexed { index, item ->
                    val isSelected = index == highlightIndex
                    val tint by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        label = "navTint"
                    )
                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.12f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "navIconScale"
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { appState.navigateToTopLevelDestination(item) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val iconVector = if (isSelected) item.selectedIcon else item.unselectedIcon
                        val unread = chatUnreadVM.count
                        val icon: @Composable () -> Unit = {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = stringResource(id = item.titleRes),
                                tint = tint,
                                modifier = Modifier
                                    .size(22.dp)
                                    .scale(iconScale)
                            )
                        }
                        if (item == NavItems.Chat && unread > 0) {
                            BadgedBox(
                                badge = {
                                    Badge {
                                        Text(text = if (unread > 99) "99+" else unread.toString())
                                    }
                                }
                            ) { icon() }
                        } else {
                            icon()
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(id = item.titleRes),
                            fontSize = 10.sp,
                            fontFamily = robotoFontFamily,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = tint,
                            maxLines = 1,
                            style = TextStyle(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
fun NavigationGraph(
    appState: ApplicationState,
    restartApp: () -> Unit,
    onSettingsClick: () -> Unit,
    onAddFlowActiveChange: (Boolean) -> Unit = {}
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
                // Post-card image → details pager shared-element (keyed per post).
                val postImageModifier: @Composable (Int) -> Modifier = { postId ->
                    Modifier.sharedElement(
                        rememberSharedContentState(key = "post_image_$postId"),
                        animatedVisibilityScope = this@composable
                    )
                }
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
                    postImageModifier = postImageModifier,
                    onLoginRequired = {
                        navController.navigate(Screen.Welcome.route)
                    },
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
                // Matches the Home card image with the same key — the first pager
                // image morphs from/to the tapped card.
                val sharedImageModifier = Modifier.sharedElement(
                    rememberSharedContentState(key = "post_image_${param ?: 0}"),
                    animatedVisibilityScope = this@composable
                )
                ProductDetailsRoute(
                    productId = param ?: 0,
                    imageSharedModifier = sharedImageModifier,
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
                    onCreatingChange = onAddFlowActiveChange,
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
                route = Screen.SavedPosts.route,
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) }
            ) { entry ->
                // Profile's "My ads" deep-links here with the My Posts tab preselected.
                val startOnMyPosts = entry.savedStateHandle.get<Boolean>("start_my_posts") ?: false
                SavedPostsRoute(
                    startOnMyPosts = startOnMyPosts,
                    navigateToProduct = {
                        navController.navigate(Screen.ProductDetails(it).route)
                    },
                    onPromote = { postId ->
                        navController.navigate(Screen.Boost(postId).route)
                    },
                    onWhoInterested = { postId ->
                        navController.navigate(Screen.Interested(postId).route)
                    },
                    onAnalytics = {
                        navController.navigate(Screen.Analytics.route)
                    }
                )
            }

            composable(
                route = Screen.Boost.ROUTE,
                arguments = listOf(
                    navArgument("postId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getLong("postId") ?: return@composable
                uz.promo.selling.ui.main.myPosts.BoostRoute(
                    postId = postId,
                    onBack = navController::popBackStack
                )
            }

            composable(route = Screen.Premium.route) {
                uz.promo.selling.ui.premium.PremiumRoute(
                    onBack = navController::popBackStack
                )
            }

            composable(route = Screen.Analytics.route) {
                uz.promo.selling.ui.premium.AnalyticsRoute(
                    onBack = navController::popBackStack,
                    onGetPremium = { navController.navigate(Screen.Premium.route) },
                    onPostClick = { navController.navigate(Screen.Interested(it).route) },
                )
            }

            composable(
                route = Screen.Interested.ROUTE,
                arguments = listOf(navArgument("postId") { type = NavType.LongType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getLong("postId") ?: return@composable
                uz.promo.selling.ui.premium.InterestedRoute(
                    postId = postId,
                    onBack = navController::popBackStack,
                    onGetPremium = { navController.navigate(Screen.Premium.route) },
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
                        // Open the merged screen on the My Posts tab.
                        navController.navigate(Screen.SavedPosts.route)
                        navController.getBackStackEntry(Screen.SavedPosts.route)
                            .savedStateHandle["start_my_posts"] = true
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
                    },
                    toPremium = {
                        navController.navigate(Screen.Premium.route)
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
                        if (googleState.needsProfile) {
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
                            context, localizedError(context, googleState.error), android.widget.Toast.LENGTH_LONG
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
                        if (googleState.needsProfile) {
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
                            context, localizedError(context, googleState.error), android.widget.Toast.LENGTH_LONG
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
                    navigateToMainScreen = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
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

                LaunchedEffect(Unit) { googleAuthVM.loadPrefill() }

                CompleteProfileScreen(
                    isLoading = googleState.isLoading,
                    prefillName = googleState.prefillName,
                    prefillLastname = googleState.prefillLastname,
                    prefillPhotoUrl = googleState.prefillPhotoUrl,
                    onSubmit = { name, lastname, phone, photoUri ->
                        googleAuthVM.completeProfile(name, lastname, phone, photoUri) {
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
                    // Chat is a primary bottom-bar tab now — no back arrow.
                    showBackButton = false,
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
