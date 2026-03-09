package org.don.onlineTrade.ui.profile.myPosts

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.launch
import org.don.onlineTrade.R
import org.don.onlineTrade.ui.filterCategory.ComposeLottieAnimation
import org.don.onlineTrade.ui.home.HomeViewModel
import org.don.onlineTrade.ui.home.ProductItem
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading

@Composable
fun MyPostsScreenRoute(
    modifier: Modifier = Modifier,
    onItemClicked: (Int) -> Unit,
) {
    MyPostsScreen(
        modifier = modifier,
        onItemClicked = onItemClicked,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostsScreen(
    modifier: Modifier = Modifier,
    onItemClicked: (Int) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    myPostVM: MyPostViewModel = hiltViewModel(),
) {
    val paddingValues = WindowInsets.systemBars.asPaddingValues()

    val pagerState = homeViewModel.pagerState
    val updateState = myPostVM.state.value

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var postId by remember {
        mutableIntStateOf(-1)
    }
    val context = LocalContext.current
    val prioritized: String = stringResource(id = R.string.it_is_already_prioritized)


    LaunchedEffect(key1 = homeViewModel){
        homeViewModel.loadNextItems(isMyPosts = true)
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier
                .padding(horizontal = MaterialTheme.spacing.dimen8Dp)
                .fillMaxSize(),
        ) {
            items(pagerState.items.size) { i ->
                val item = pagerState.items[i]
                LaunchedEffect(rememberLazyGridState()) {
                    if (i >= pagerState.items.size - 1 && !pagerState.endReached && !pagerState.isLoading) {
                        homeViewModel.loadNextItems(
                            isMyPosts = true
                        )
                    }
                }
                ProductItem(
                    item,
                    onItemClicked = onItemClicked,
                    isMyPosts = true,
                    onItemLongLicked = {
                        if (!item.isPrioritized && item.status == 1){
                            postId = it
                            showBottomSheet = true
                        }
                        if (item.isPrioritized){
                            Toast.makeText(context, prioritized, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
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
        if (!pagerState.isLoading && pagerState.endReached && pagerState.items.isEmpty()){
            ComposeLottieAnimation(Modifier)
        }
        FreeLoading(updateState.isLoading)
        FreeLoading(pagerState.isLoading)
    }


    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            PrioritizeDialogContent{ period ->
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible){
                        showBottomSheet = false
                        myPostVM.updateValues(postId = postId.toLong(), period = period)
                    }
                }
            }
        }
    }
}

