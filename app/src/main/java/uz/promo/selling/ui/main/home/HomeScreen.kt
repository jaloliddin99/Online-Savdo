package uz.promo.selling.ui.main.home

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import uz.promo.selling.R
import uz.promo.selling.data.location.GpsCheckHelper
import uz.promo.selling.data.location.checkGpsEnabled
import uz.promo.selling.ui.categoriesList.Categories
import uz.promo.selling.ui.main.home.homeItems.HomeSearchBar
import uz.promo.selling.ui.main.home.homeItems.ShimmerCategoriesRow
import uz.promo.selling.ui.main.home.homeItems.ShimmerNearPostsRow
import uz.promo.selling.ui.main.home.homeItems.ShimmerProductGrid
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.ui.theme.spacing
import uz.promo.selling.utils.LocaleManager.FLAG_HAS_DATA
import uz.promo.selling.utils.SharedPref
import uz.promo.selling.data.remote.models.nearPost.NearPostsData
import uz.promo.selling.utils.hasPermissionForLocation
import uz.promo.selling.utils.runTimePermission.RunTimePermission


@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    navigateToProduct: (Int) -> Unit,
    navigateToCategory: (Int) -> Unit,
    onSearchClick: () -> Unit = {},
    onMapClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {},
    searchBarModifier: Modifier = Modifier,
    postImageModifier: @Composable (Int) -> Modifier = { Modifier },
    onLoginRequired: () -> Unit = {},
) {
    HomeScreen(
        modifier = modifier,
        homeViewModel = homeViewModel,
        navigateToProduct = navigateToProduct,
        navigateToCategory = navigateToCategory,
        onSearchClick = onSearchClick,
        onMapClick = onMapClick,
        onNotificationClick = onNotificationClick,
        onMessagesClick = onMessagesClick,
        searchBarModifier = searchBarModifier,
        postImageModifier = postImageModifier,
        onLoginRequired = onLoginRequired,
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier,
    homeViewModel: HomeViewModel,
    navigateToProduct: (Int) -> Unit,
    navigateToCategory: (Int) -> Unit,
    onSearchClick: () -> Unit = {},
    onMapClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {},
    searchBarModifier: Modifier = Modifier,
    postImageModifier: @Composable (Int) -> Modifier = { Modifier },
    onLoginRequired: () -> Unit = {},
) {

    val state = homeViewModel.state.value
    val stateNear = homeViewModel.stateNear.value
    val products = homeViewModel.productsFlow.collectAsLazyPagingItems()
    // rememberSaveable-backed: survives the tab-switch composition teardown so we
    // restore the previous scroll position instead of re-measuring from item 0.
    val scrollState = rememberLazyGridState()

    val context = LocalContext.current

    val chatUnreadViewModel: uz.promo.selling.ui.chat.ChatUnreadViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    // Poll the unread count only while Home is actually resumed on-screen.
    // repeatOnLifecycle cancels the loop when the app is backgrounded or the user
    // switches tabs, and restarts it on return — no background battery/data drain.
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.RESUMED) {
            while (true) {
                chatUnreadViewModel.refresh()
                kotlinx.coroutines.delay(15000)
            }
        }
    }

    // When Home returns to the foreground (e.g. back from the map), refresh the
    // "near you" strip for the location the user picked, if any.
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.RESUMED) {
            homeViewModel.refreshNearPostsForSelectedLocation()
        }
    }

    LaunchedEffect(state.error) {
        if (state.error.isNotBlank()) {
            Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
        }
    }

    // Like feedback: snackbar + haptic tick without leaving the feed.
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // Collapsing glass header: the search row scrubs up/down with the scroll and
    // spring-snaps to fully shown / fully hidden when the gesture ends.
    val homeHazeState = remember { HazeState() }
    val surfaceColor = MaterialTheme.colorScheme.surface
    val headerCollapsePx = with(LocalDensity.current) { 64.dp.toPx() }
    val headerOffset = remember { mutableFloatStateOf(0f) } // 0 = expanded … -range = collapsed
    val headerConnection = remember(headerCollapsePx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val current = headerOffset.floatValue
                val target = (current + available.y).coerceIn(-headerCollapsePx, 0f)
                val consumed = target - current
                if (consumed != 0f) headerOffset.floatValue = target
                return Offset(0f, consumed)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                // Snap to the nearest end state with a spring (same feel as the
                // profile avatar). A new touch cancels this automatically.
                val current = headerOffset.floatValue
                if (current > -headerCollapsePx && current < 0f) {
                    val target = if (current < -headerCollapsePx / 2f) -headerCollapsePx else 0f
                    animate(
                        initialValue = current,
                        targetValue = target,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) { value, _ -> headerOffset.floatValue = value }
                }
                return Velocity.Zero
            }
        }
    }
    val statusTop = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(headerConnection)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = homeHazeState),
            // Top: room for the glass header (52dp title + 64dp search row);
            // bottom: clearance for the floating glass nav bar.
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                top = statusTop + 116.dp,
                bottom = 96.dp
            ),
        ) {
            // ── Categories Section ──
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = stringResource(id = R.string.category),
                    style = TextStyle.Default,
                    modifier = Modifier.padding(
                        start = MaterialTheme.spacing.dimen8Dp,
                        bottom = MaterialTheme.spacing.dimen8Dp
                    ),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = MaterialTheme.spacing.dimen16Sp
                )
            }
            if (state.parentCategoryList != null) {
                item(key = "Categories", span = { GridItemSpan(2) }) {
                    Categories(
                        state.parentCategoryList,
                        navigateToCategory = navigateToCategory
                    )
                }
            } else {
                item(span = { GridItemSpan(2) }) {
                    ShimmerCategoriesRow()
                }
            }

            item(span = { GridItemSpan(2) }) {
                Text(
                    text = stringResource(id = R.string.near_you),
                    modifier = Modifier.padding(
                        start = MaterialTheme.spacing.dimen8Dp,
                        top = MaterialTheme.spacing.dimen8Dp,
                        bottom = MaterialTheme.spacing.dimen8Dp
                    ),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = MaterialTheme.spacing.dimen16Sp,
                    fontFamily = robotoFontFamily
                )
            }
            item(span = { GridItemSpan(2) }) {
                val activity = LocalActivity.current as ComponentActivity
                val hasPicked = SharedPref.hasPickedLocation
                val hasNotPermission = !hasPermissionForLocation(context)
                val gpsNotEnabled = !checkGpsEnabled(activity)

                when {
                    // User picked a location on the map → show its "near you"
                    // (refreshed on resume; no GPS/permission needed).
                    hasPicked -> NearPostsContent(stateNear.getNearPost, navigateToProduct)

                    hasNotPermission || gpsNotEnabled -> GPSEnableView(
                        onPermissionClicked = {
                            if (!hasNotPermission) {
                                GpsCheckHelper(activity).turnOnGpsDialogRequest()
                                homeViewModel.locationObserve()
                                homeViewModel.startLocationUpdates()
                            }
                            RunTimePermission().locationPermission(
                                onPermissionEnabled = {
                                    GpsCheckHelper(activity).turnOnGpsDialogRequest()
                                    homeViewModel.locationObserve()
                                    homeViewModel.startLocationUpdates()
                                },
                                onPermissionNotEnabled = {},
                                activity
                            )
                        },
                        onTurnOnClicked = {
                            GpsCheckHelper(activity).turnOnGpsDialogRequest()
                            homeViewModel.locationObserve()
                            homeViewModel.startLocationUpdates()
                        },
                        hasNotPermission
                    )

                    else -> {
                        if (!FLAG_HAS_DATA) {
                            FLAG_HAS_DATA = true
                            homeViewModel.locationObserve()
                            homeViewModel.startLocationUpdates()
                        }
                        NearPostsContent(stateNear.getNearPost, navigateToProduct)
                    }
                }
            }

            // ── All Posts Section ──
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = stringResource(id = R.string.all_posts),
                    style = TextStyle.Default,
                    modifier = Modifier.padding(
                        start = MaterialTheme.spacing.dimen8Dp,
                        top = MaterialTheme.spacing.dimen8Dp
                    ),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = MaterialTheme.spacing.dimen16Sp
                )
            }
            if (products.loadState.refresh is LoadState.Loading) {
                item(span = { GridItemSpan(2) }) {
                    ShimmerProductGrid()
                }
            } else {
                items(
                    count = products.itemCount,
                    // Use peek() here, NOT products[it]. The grid resolves the key lambda
                    // for a window of items beyond the viewport; products[it] calls get(),
                    // which registers a Paging access and triggers an append. That append
                    // grows itemCount, the key lambda runs at the new edge, calls get()
                    // again, and the feed eagerly loads every page to the end without the
                    // user scrolling. peek() returns the item without triggering a load.
                    key = { index -> products.peek(index)?.id ?: index }
                ) { i ->
                    products[i]?.let { item ->
                        ProductItem(
                            item,
                            onItemClicked = navigateToProduct,
                            onItemLongLicked = {},
                            imageModifier = postImageModifier(item.id),
                            // Session toggles win; otherwise the server's answer.
                            isLiked = homeViewModel.likedPosts[item.id] ?: (item.isLiked ?: false),
                            onLikeClicked = { id ->
                                if (SharedPref.deviceToken.isEmpty()) {
                                    onLoginRequired()
                                } else {
                                    val liked = homeViewModel.toggleLike(id)
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    scope.launch {
                                        snackbarHostState.currentSnackbarData?.dismiss()
                                        snackbarHostState.showSnackbar(
                                            message = context.getString(
                                                if (liked) R.string.post_liked else R.string.post_unliked
                                            ),
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
                if (products.loadState.append is LoadState.Loading) {
                    item(span = { GridItemSpan(2) }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                item {
                    Spacer(modifier = modifier.height(MaterialTheme.spacing.dimen16Dp))
                }
            }
        }

        // Floating glass header — brand title row + collapsing search field.
        // The feed scrolls underneath and blurs through it.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .hazeEffect(state = homeHazeState) {
                    backgroundColor = surfaceColor
                    blurRadius = 22.dp
                    noiseFactor = 0f
                    tints = listOf(HazeTint(surfaceColor.copy(alpha = 0.50f)))
                    fallbackTint = HazeTint(surfaceColor.copy(alpha = 0.90f))
                }
        ) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Selling.uz",
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.graphicsLayer {
                        // Shrinks toward its left edge while the search row docks
                        // up; grows back as it returns.
                        val progress = -headerOffset.floatValue / headerCollapsePx
                        val scale = lerp(1f, 0.78f, progress)
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = TransformOrigin(0f, 0.5f)
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            // Collapsing search row: slides up under the title and gives back its
            // height as it goes (scrubbed by scroll, spring-snapped on release).
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clipToBounds()
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        val offset = headerOffset.floatValue
                        val height = (placeable.height + offset).roundToInt().coerceAtLeast(0)
                        layout(placeable.width, height) {
                            placeable.placeRelative(0, offset.roundToInt())
                        }
                    }
                    .graphicsLayer {
                        alpha = 1f - (-headerOffset.floatValue / headerCollapsePx)
                    }
            ) {
                HomeSearchBar(
                    onSearchClick = onSearchClick,
                    onMapClick = onMapClick,
                    searchBarModifier = searchBarModifier,
                    hazeState = homeHazeState,
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                // Sit above the floating glass bottom bar.
                .padding(bottom = 84.dp)
        )
    }
}


/** Renders the "near you" strip: shimmer while loading, an empty note, or the list. */
@Composable
private fun NearPostsContent(
    nearPosts: List<NearPostsData>?,
    navigateToProduct: (Int) -> Unit,
) {
    when {
        // Still loading (no response yet) — keep the shimmer.
        nearPosts == null -> ShimmerNearPostsRow()
        // Loaded, but the backend returned an empty list — explain instead of
        // rendering nothing.
        nearPosts.isEmpty() -> NearPostsEmpty()
        else -> NearPosts(
            state = nearPosts,
            navigateToCategory = navigateToProduct
        )
    }
}

@Composable
fun GPSEnableView(
    onPermissionClicked: () -> Unit,
    onTurnOnClicked: () -> Unit,
    hasNotPermission: Boolean,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                start = MaterialTheme.spacing.dimen8Dp,
                end = MaterialTheme.spacing.dimen8Dp,
                top = MaterialTheme.spacing.dimen8Dp
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            val desc: Int = if (hasNotPermission)
                R.string.gps_description else R.string.gps_turn_on_desc
            val title: Int = if (hasNotPermission) R.string.allow else R.string.turn_on
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    imageVector = Icons.Rounded.Warning, contentDescription = null,
                    modifier = Modifier
                        .width(36.dp)
                        .height(36.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = desc),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 17.sp
                )
            }
            Text(
                text = stringResource(id = title),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                modifier = Modifier
                    .wrapContentWidth()
                    .align(alignment = Alignment.End)
                    .clickable {
                        if (hasNotPermission) onPermissionClicked.invoke() else onTurnOnClicked.invoke()
                    },
                textAlign = TextAlign.End
            )
        }
    }
}
