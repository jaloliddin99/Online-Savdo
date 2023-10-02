package org.don.onlineTrade.ui.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading


@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    navigateToProduct: (Int) -> Unit,
    navigateToCategory: (Int) -> Unit
) {
    HomeScreen(
        modifier = modifier,
        navigateToProduct = navigateToProduct,
        navigateToCategory = navigateToCategory
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier,
    navigateToProduct: (Int) -> Unit,
    navigateToCategory: (Int) -> Unit,
) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val state = viewModel.state.value
    val isFeedLoading = state.isLoading

    val context = LocalContext.current

    val pagerState = viewModel.pagerState
    val scrollState = rememberLazyGridState()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.dimen8Dp)
        ) {
            if (state.registerMain != null) {
                item(span = { GridItemSpan(2) }) {
                    Categories(
                        state.registerMain,
                        navigateToCategory = navigateToCategory
                    )
                }
            }
            Log.d("TAG", "HomeScreendawdawdawdawd ${pagerState.items.size}")

            items(pagerState.items.size) { i ->
                val item = pagerState.items[i]
                LaunchedEffect(scrollState) {
                    if (i >= pagerState.items.size - 1 && !pagerState.endReached && !pagerState.isLoading) {
                        Log.d("TAG", "HomeScreendawdawdawdawd2 ${pagerState.items.size}")
                        viewModel.loadNextItems()
                    }
                }

                ProductItem(item, onItemClicked = navigateToProduct)
            }
            item(span = { GridItemSpan(2) }) {
                if (pagerState.isLoading) {
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
        FreeLoading(isFeedLoading = isFeedLoading)
    }


    if (state.error.isNotBlank()) {
        Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
    }


}
