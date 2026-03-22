package org.don.onlineTrade.ui.main.myPosts

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import org.don.onlineTrade.R
import org.don.onlineTrade.ui.TopAppBar
import org.don.onlineTrade.ui.filterCategory.ComposeLottieAnimation
import org.don.onlineTrade.ui.main.home.ProductItem
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.LoadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostsScreenRoute(
    modifier: Modifier = Modifier,
    onItemClicked: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = stringResource(R.string.my_orders),
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )
        MyPostsScreen(
            modifier = modifier.weight(1f),
            onItemClicked = onItemClicked,
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
    val selectedStatus by myPostVM.selectedStatus.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var postId by remember { mutableIntStateOf(-1) }
    val context = LocalContext.current
    val prioritized: String = stringResource(id = R.string.it_is_already_prioritized)

    val isRefreshing = myPosts.loadState.refresh is LoadState.Loading

    Column(modifier = modifier.fillMaxSize()) {
        // Status filter tabs
        StatusFilterRow(
            selectedStatus = selectedStatus,
            onStatusSelected = { myPostVM.setStatusFilter(it) }
        )

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { myPosts.refresh() },
            modifier = Modifier.fillMaxSize()
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
fun StatusFilterRow(
    selectedStatus: Int?,
    onStatusSelected: (Int?) -> Unit
) {
    data class StatusTab(val status: Int?, val labelResId: Int)

    val tabs = listOf(
        StatusTab(null, R.string.all),
        StatusTab(1, R.string.published),
        StatusTab(0, R.string.pending),
        StatusTab(2, R.string.rejected),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEach { tab ->
            FilterChip(
                selected = selectedStatus == tab.status,
                onClick = { onStatusSelected(tab.status) },
                label = { Text(stringResource(tab.labelResId)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}
