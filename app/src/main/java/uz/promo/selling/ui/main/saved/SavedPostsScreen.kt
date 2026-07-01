package uz.promo.selling.ui.main.saved

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import uz.promo.selling.R
import uz.promo.selling.ui.main.myPosts.MyPostViewModel
import uz.promo.selling.ui.main.myPosts.MyPostsScreen
import uz.promo.selling.ui.main.myPosts.StatusFilterDropdown
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.ui.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity

private const val TAB_SAVED = 0
private const val TAB_MY_POSTS = 1

/**
 * Merged "Saved" + "My Posts" screen. A rounded segmented toggle on top switches
 * between the two lists (modeled on the ishUz read/unread toggle). The My Posts
 * status filter lives in the header and only shows while that tab is active.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPostsRoute(
    startOnMyPosts: Boolean = false,
    navigateToProduct: (Int) -> Unit,
    onPromote: (Long) -> Unit = {},
    onWhoInterested: (Long) -> Unit = {},
    onAnalytics: () -> Unit = {},
) {
    var selectedTab by rememberSaveable {
        mutableIntStateOf(if (startOnMyPosts) TAB_MY_POSTS else TAB_SAVED)
    }
    val myPostVM = hiltViewModel<MyPostViewModel>()
    val selectedStatus by myPostVM.selectedStatus.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }

    // Collapsing-tab plumbing: the app bar stays pinned while the toggle row
    // slides away as the list scrolls up (enter-always behavior).
    val density = LocalDensity.current
    val toggleAreaHeight = 64.dp
    val toggleHeightPx = with(density) { toggleAreaHeight.toPx() }
    var heightOffset by remember { mutableFloatStateOf(0f) } // -toggleHeightPx..0
    val collapseConnection = remember(toggleHeightPx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (delta < 0f) { // scrolling content up → collapse toggle first
                    val prev = heightOffset
                    heightOffset = (heightOffset + delta).coerceIn(-toggleHeightPx, 0f)
                    return Offset(0f, heightOffset - prev)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = available.y
                if (delta > 0f) { // list at top, scrolling down → expand toggle
                    val prev = heightOffset
                    heightOffset = (heightOffset + delta).coerceIn(-toggleHeightPx, 0f)
                    return Offset(0f, heightOffset - prev)
                }
                return Offset.Zero
            }
        }
    }
    val currentToggleHeight = with(density) { (toggleHeightPx + heightOffset).toDp() }
    val toggleAlpha = if (toggleHeightPx == 0f) 1f else (toggleHeightPx + heightOffset) / toggleHeightPx

    Column(modifier = Modifier.fillMaxSize()) {
        // Pinned app bar.
        TopAppBar(
            title = stringResource(R.string.nav_saved_posts),
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Collapsing toggle area (clipped/faded as it shrinks).
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(currentToggleHeight)
                .clipToBounds()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(toggleAreaHeight)
                    .alpha(toggleAlpha)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SegmentedToggle(
                    options = listOf(
                        stringResource(R.string.nav_saved),
                        stringResource(R.string.nav_my_posts),
                    ),
                    selectedIndex = selectedTab,
                    onSelect = { selectedTab = it },
                    modifier = Modifier.weight(1f),
                )

                // Status filter — only relevant on the My Posts tab.
                AnimatedVisibility(
                    visible = selectedTab == TAB_MY_POSTS,
                    enter = fadeIn(tween(150)),
                    exit = fadeOut(tween(150)),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onAnalytics) {
                            Icon(
                                imageVector = Icons.Outlined.BarChart,
                                contentDescription = stringResource(R.string.analytics_title),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Box {
                            IconButton(onClick = { showFilterMenu = true }) {
                                Icon(
                                    imageVector = Icons.Outlined.FilterList,
                                    contentDescription = stringResource(R.string.all),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            StatusFilterDropdown(
                                expanded = showFilterMenu,
                                selectedStatus = selectedStatus,
                                onStatusSelected = { status ->
                                    myPostVM.setStatusFilter(status)
                                    showFilterMenu = false
                                },
                                onDismiss = { showFilterMenu = false }
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .nestedScroll(collapseConnection)
        ) {
            when (selectedTab) {
                TAB_SAVED -> SavedScreen(
                    modifier = Modifier.fillMaxSize(),
                    navigateToProduct = navigateToProduct,
                )
                else -> MyPostsScreen(
                    modifier = Modifier.fillMaxSize(),
                    onItemClicked = navigateToProduct,
                    onPromote = onPromote,
                    onWhoInterested = onWhoInterested,
                    myPostVM = myPostVM,
                )
            }
        }
    }
}

/** Rounded pill segmented control (ishUz-style). */
@Composable
fun SegmentedToggle(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        options.forEachIndexed { index, label ->
            ToggleSegment(
                label = label,
                isSelected = index == selectedIndex,
                onClick = { onSelect(index) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ToggleSegment(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
        label = "segment-bg"
    )
    val fg by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        label = "segment-fg"
    )
    Box(
        modifier = modifier
            .fillMaxHeight()
            .then(
                if (isSelected) Modifier.shadow(2.dp, RoundedCornerShape(50)) else Modifier
            )
            .clip(RoundedCornerShape(50))
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontFamily = robotoFontFamily,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            fontSize = 14.sp,
            color = fg,
            maxLines = 1,
        )
    }
}
