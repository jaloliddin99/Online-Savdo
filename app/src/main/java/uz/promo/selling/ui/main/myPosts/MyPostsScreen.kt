package uz.promo.selling.ui.main.myPosts

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import uz.promo.selling.R
import uz.promo.selling.ui.TopAppBar
import uz.promo.selling.ui.filterCategory.ComposeLottieAnimation
import uz.promo.selling.ui.main.home.ProductItem
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.ui.theme.spacing
import uz.promo.selling.utils.FreeLoading
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.LoadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostsScreenRoute(
    modifier: Modifier = Modifier,
    onItemClicked: (Int) -> Unit,
    myPostVM: MyPostViewModel = hiltViewModel(),
) {
    val selectedStatus by myPostVM.selectedStatus.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = stringResource(R.string.my_orders),
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            ),
            actionContent = {
                Box {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = "Filter",
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
        )

        MyPostsScreen(
            modifier = modifier.weight(1f),
            onItemClicked = onItemClicked,
            myPostVM = myPostVM,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostsScreen(
    modifier: Modifier = Modifier,
    onItemClicked: (Int) -> Unit,
    onPromote: (Long) -> Unit = {},
    onWhoInterested: (Long) -> Unit = {},
    myPostVM: MyPostViewModel = hiltViewModel(),
) {
    val myPosts = myPostVM.myPostsFlow.collectAsLazyPagingItems()
    val updateState = myPostVM.state.value

    val actionSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    var actionPost by remember { mutableStateOf<uz.promo.selling.data.remote.models.getPublicProducts.Content?>(null) }
    val context = LocalContext.current
    val markedSoldMsg = stringResource(id = R.string.marked_as_sold_success)
    val reactivatedMsg = stringResource(id = R.string.reactivated_success)
    val errorMsg = stringResource(id = R.string.something_went_wrong)

    // Shared actions, used by both the long-press sheet and the ⋮ card menu.
    fun markSold(post: uz.promo.selling.data.remote.models.getPublicProducts.Content) {
        myPostVM.markPostSold(post.id.toLong()) { ok ->
            Toast.makeText(context, if (ok) markedSoldMsg else errorMsg, Toast.LENGTH_SHORT).show()
            if (ok) myPosts.refresh()
        }
    }
    fun activate(post: uz.promo.selling.data.remote.models.getPublicProducts.Content) {
        myPostVM.activatePost(post.id.toLong()) { ok ->
            Toast.makeText(context, if (ok) reactivatedMsg else errorMsg, Toast.LENGTH_SHORT).show()
            if (ok) myPosts.refresh()
        }
    }

    val isRefreshing = myPosts.loadState.refresh is LoadState.Loading

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { myPosts.refresh() },
        modifier = modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(
                count = myPosts.itemCount,
                // peek() avoids triggering a Paging load during key resolution, which
                // would otherwise eagerly load every page. See HomeScreen.
                key = { index -> myPosts.peek(index)?.id ?: index }
            ) { i ->
                myPosts[i]?.let { item ->
                    Box {
                        ProductItem(
                            item,
                            onItemClicked = onItemClicked,
                            isMyPosts = true,
                            onItemLongLicked = {
                                if (item.status == 1 || item.status == 3 || item.status == 4) {
                                    actionPost = item
                                }
                            }
                        )
                        // Visible action affordance so users discover post operations
                        // without needing to long-press.
                        PostCardMenu(
                            post = item,
                            modifier = Modifier.align(Alignment.TopEnd),
                            onMarkSold = { markSold(item) },
                            onPromote = { onPromote(item.id.toLong()) },
                            onReactivate = { activate(item) },
                            onWhoInterested = { onWhoInterested(item.id.toLong()) },
                        )
                    }
                }
            }
            if (myPosts.loadState.append is LoadState.Loading) {
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
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen16Dp))
            }
        }
        if (myPosts.loadState.refresh is LoadState.NotLoading && myPosts.itemCount == 0) {
            ComposeLottieAnimation(Modifier)
        }
        FreeLoading(updateState.isLoading)
    }

    actionPost?.let { post ->
        ModalBottomSheet(
            onDismissRequest = { actionPost = null },
            sheetState = actionSheetState
        ) {
            PostActionsSheetContent(
                status = post.status,
                isPrioritized = post.isPrioritized,
                onMarkSold = {
                    actionPost = null
                    markSold(post)
                },
                onActivate = {
                    actionPost = null
                    activate(post)
                },
                onPrioritize = {
                    actionPost = null
                    onPromote(post.id.toLong())
                }
            )
        }
    }
}

/**
 * A discoverable ⋮ action button overlaid on a My Posts card. Opens a dropdown of
 * the operations available for the post's status (active → Mark sold / Promote;
 * sold or expired → Re-activate). Hidden for statuses with no actions (pending,
 * rejected).
 */
@Composable
private fun PostCardMenu(
    post: uz.promo.selling.data.remote.models.getPublicProducts.Content,
    modifier: Modifier = Modifier,
    onMarkSold: () -> Unit,
    onPromote: () -> Unit,
    onReactivate: () -> Unit,
    onWhoInterested: () -> Unit,
) {
    if (post.status != 1 && post.status != 3 && post.status != 4) return
    var open by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .padding(top = 20.dp, end = 12.dp)
                .size(30.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.38f))
                .clickable { open = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.actions),
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            if (post.status == 1) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.mark_as_sold)) },
                    leadingIcon = { Icon(Icons.Outlined.CheckCircle, null) },
                    onClick = { open = false; onMarkSold() }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.prioritize)) },
                    leadingIcon = { Icon(Icons.Outlined.Star, null) },
                    onClick = { open = false; onPromote() }
                )
            }
            if (post.status == 3 || post.status == 4) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.reactivate_post)) },
                    leadingIcon = { Icon(Icons.Outlined.Refresh, null) },
                    onClick = { open = false; onReactivate() }
                )
            }
            DropdownMenuItem(
                text = { Text(stringResource(R.string.whos_interested)) },
                leadingIcon = { Icon(Icons.Outlined.Visibility, null) },
                onClick = { open = false; onWhoInterested() }
            )
        }
    }
}

@Composable
private fun PostActionsSheetContent(
    status: Int,
    isPrioritized: Boolean,
    onMarkSold: () -> Unit,
    onActivate: () -> Unit,
    onPrioritize: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 32.dp)
    ) {
        if (status == 1) {
            PostActionRow(
                icon = Icons.Outlined.CheckCircle,
                label = stringResource(R.string.mark_as_sold),
                onClick = onMarkSold
            )
            Spacer(modifier = Modifier.height(8.dp))
            PostActionRow(
                icon = Icons.Outlined.Star,
                label = stringResource(R.string.prioritize),
                onClick = onPrioritize
            )
        }
        if (status == 3 || status == 4) {
            PostActionRow(
                icon = Icons.Outlined.Refresh,
                label = stringResource(R.string.reactivate_post),
                onClick = onActivate
            )
        }
    }
}

@Composable
private fun PostActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp
        )
    }
}

@Composable
fun StatusFilterDropdown(
    expanded: Boolean,
    selectedStatus: Int?,
    onStatusSelected: (Int?) -> Unit,
    onDismiss: () -> Unit
) {
    data class StatusOption(val status: Int?, val labelResId: Int, val color: Color)

    val options = listOf(
        StatusOption(null, R.string.all, Color.Unspecified),
        StatusOption(1, R.string.published, Color(0xFF4CAF50)),
        StatusOption(0, R.string.pending, Color(0xFFFFA726)),
        StatusOption(3, R.string.sold, Color(0xFF42A5F5)),
        StatusOption(4, R.string.expired, Color(0xFF9E9E9E)),
        StatusOption(2, R.string.rejected, Color(0xFFEF5350)),
    )

    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(12.dp))
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
        ) {
            options.forEach { option ->
                val isSelected = selectedStatus == option.status
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (option.color != Color.Unspecified) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(option.color)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            Text(
                                text = stringResource(option.labelResId),
                                fontFamily = robotoFontFamily,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 14.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    onClick = { onStatusSelected(option.status) },
                    trailingIcon = {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}
