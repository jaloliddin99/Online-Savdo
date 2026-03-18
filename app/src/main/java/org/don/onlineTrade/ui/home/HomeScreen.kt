package org.don.onlineTrade.ui.home

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.don.onlineTrade.R
import org.don.onlineTrade.data.location.GpsCheckHelper
import org.don.onlineTrade.data.location.checkGpsEnabled
import org.don.onlineTrade.ui.home.search.HomeSearchBar
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.LocaleManager.FLAG_HAS_DATA
import org.don.onlineTrade.utils.hasPermissionForLocation
import org.don.onlineTrade.utils.runTimePermission.RunTimePermission


@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    navigateToProduct: (Int) -> Unit,
    navigateToCategory: (Int) -> Unit,
    onSearchClick: () -> Unit = {},
    onMapClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    searchBarModifier: Modifier = Modifier,
) {
    HomeScreen(
        modifier = modifier,
        homeViewModel = homeViewModel,
        navigateToProduct = navigateToProduct,
        navigateToCategory = navigateToCategory,
        onSearchClick = onSearchClick,
        onMapClick = onMapClick,
        onNotificationClick = onNotificationClick,
        searchBarModifier = searchBarModifier,
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
    searchBarModifier: Modifier = Modifier,
) {
    val viewModel = homeViewModel
    val state = viewModel.state.value
    val stateNear = viewModel.stateNear.value
    val isFeedLoading = state.isLoading

    val context = LocalContext.current

    val pagerState = viewModel.pagerState
    val scrollState = rememberLazyGridState()

    // Track scroll direction to show/hide the search bar


    LaunchedEffect(key1 = viewModel) {
        viewModel.loadNextItems()
        viewModel.getAllParentCategories()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        HomeSearchBar(
            onSearchClick = onSearchClick,
            onMapClick = onMapClick,
            onNotificationClick = onNotificationClick,
            searchBarModifier = searchBarModifier,
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = scrollState,
            modifier = Modifier.weight(1f),
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
                val hasNotPermission = !hasPermissionForLocation(context)
                val gpsNotEnabled = !checkGpsEnabled(activity)

                if (hasNotPermission || gpsNotEnabled) {
                    GPSEnableView(
                        onPermissionClicked = {
                            if (!hasNotPermission) {
                                GpsCheckHelper(activity).turnOnGpsDialogRequest()
                                viewModel.locationObserve()
                                viewModel.startLocationUpdates()
                            }
                            RunTimePermission().locationPermission(
                                onPermissionEnabled = {
                                    GpsCheckHelper(activity).turnOnGpsDialogRequest()
                                    viewModel.locationObserve()
                                    viewModel.startLocationUpdates()
                                },
                                onPermissionNotEnabled = {},
                                activity
                            )
                        },
                        onTurnOnClicked = {
                            GpsCheckHelper(activity).turnOnGpsDialogRequest()
                            viewModel.locationObserve()
                            viewModel.startLocationUpdates()
                        },
                        hasNotPermission
                    )
                } else {
                    if (!FLAG_HAS_DATA) {
                        FLAG_HAS_DATA = true
                        viewModel.locationObserve()
                        viewModel.startLocationUpdates()
                    }
                    if (stateNear.getNearPost != null) {
                        NearPosts(
                            state = stateNear.getNearPost!!,
                            navigateToCategory = navigateToProduct
                        )
                    } else {
                        ShimmerNearPostsRow()
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
            if (pagerState.items.isEmpty() && !pagerState.endReached) {
                item(span = { GridItemSpan(2) }) {
                    ShimmerProductGrid()
                }
            } else {
                items(count = pagerState.items.size,
                    key = {
                        pagerState.items[it].image.imagePath
                    }) { i ->
                    val item = pagerState.items[i]
                    LaunchedEffect(scrollState) {
                        if (i >= pagerState.items.size - 1 && !pagerState.endReached && !pagerState.isLoading) {
                            viewModel.loadNextItems()
                        }
                    }
                    ProductItem(item, onItemClicked = navigateToProduct, onItemLongLicked = {})
                }
                item(span = { GridItemSpan(2) }) {
                    if (pagerState.isLoading && pagerState.page != 0) {
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
    }


    if (state.error.isNotBlank()) {
        Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
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

