package uz.don.onlineTrade.ui.main.myPosts

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.FilterList
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
import uz.don.onlineTrade.R
import uz.don.onlineTrade.ui.TopAppBar
import uz.don.onlineTrade.ui.filterCategory.ComposeLottieAnimation
import uz.don.onlineTrade.ui.main.home.ProductItem
import uz.don.onlineTrade.ui.theme.robotoFontFamily
import uz.don.onlineTrade.ui.theme.spacing
import uz.don.onlineTrade.utils.FreeLoading
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
    myPostVM: MyPostViewModel = hiltViewModel(),
) {
    val myPosts = myPostVM.myPostsFlow.collectAsLazyPagingItems()
    val updateState = myPostVM.state.value

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var postId by remember { mutableIntStateOf(-1) }
    val context = LocalContext.current
    val prioritized: String = stringResource(id = R.string.it_is_already_prioritized)

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
                key = { myPosts[it]?.id ?: it }
            ) { i ->
                myPosts[i]?.let { item ->
                    ProductItem(
                        item,
                        onItemClicked = onItemClicked,
                        isMyPosts = true,
                        onItemLongLicked = {
                            if (!item.isPrioritized && item.status == 1) {
                                postId = it
                                showBottomSheet = true
                            }
                            if (item.isPrioritized) {
                                Toast.makeText(context, prioritized, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
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

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            PrioritizeDialogContent { period ->
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                        myPostVM.updateValues(postId = postId.toLong(), period = period)
                    }
                }
            }
        }
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
