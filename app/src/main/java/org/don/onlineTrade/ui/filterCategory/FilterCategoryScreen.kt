package org.don.onlineTrade.ui.filterCategory

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
import org.don.onlineTrade.ui.home.HomeViewModel
import org.don.onlineTrade.ui.home.ProductItem
import org.don.onlineTrade.ui.home.ScreenState
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading
import javax.inject.Inject
import javax.inject.Named


@Composable
fun FilterCategoryRoute(
    modifier: Modifier = Modifier,
    onItemClicked: (Int) -> Unit,
    categoryId: Int?
) {
    FilterCategoryScreen(
        modifier = modifier,
        onItemClicked = onItemClicked,
        categoryId = categoryId
    )
}


@Composable
fun FilterCategoryScreen(
    modifier: Modifier = Modifier,
    onItemClicked: (Int) -> Unit,
    categoryId: Int?
) {

    val homeViewModel = hiltViewModel<HomeViewModel>()
    val pagerState = homeViewModel.pagerState

    val scrollState = rememberLazyGridState()
    LaunchedEffect(key1 = homeViewModel){
        homeViewModel.loadNextItems(categoryId = categoryId)
    }
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier
                .padding(horizontal = MaterialTheme.spacing.dimen8Dp)
                .fillMaxSize(),
        ) {
            items(pagerState.items.size) { i ->
                val item = pagerState.items[i]
                LaunchedEffect(scrollState) {
                    if (i >= pagerState.items.size - 1 && !pagerState.endReached && !pagerState.isLoading) {
                        homeViewModel.loadNextItems(
                            categoryId = categoryId
                        )
                    }
                }
                ProductItem(
                    item,
                    onItemClicked = onItemClicked,
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
        FreeLoading(pagerState.isLoading)
    }

}