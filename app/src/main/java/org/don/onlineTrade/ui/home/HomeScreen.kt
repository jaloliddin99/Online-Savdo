package org.don.onlineTrade.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
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
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val state = homeViewModel.state.value
    val products = homeViewModel.collectProducts().collectAsLazyPagingItems()

    HomeScreen(
        modifier = modifier, state = state, pagingItems = products,
        navigateToProduct = navigateToProduct,
        navigateToCategory = navigateToCategory
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier,
    state: HomeScreenState,
    pagingItems: LazyPagingItems<Data>,
    navigateToProduct: (Int) -> Unit,
    navigateToCategory: (Int) -> Unit
) {
    val isFeedLoading = state.isLoading

    val context = LocalContext.current

    FreeLoading(isFeedLoading = isFeedLoading)
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.dimen8Dp),
        modifier = modifier
            .fillMaxSize()
        ) {
            if (state.registerMain != null) {
                item(span = { GridItemSpan(2) }) {
                    Categories(
                        state.registerMain,
                        navigateToCategory = navigateToCategory
                    )
                }
            }

            val dataSet = pagingItems.itemSnapshotList.items
            if (dataSet.isNotEmpty()) {
                items(dataSet.size - 1) {
                    ProductItem(dataSet[it], onItemClicked = navigateToProduct)
                }
            }
            item {
                Spacer(modifier = modifier.height(MaterialTheme.spacing.dimen16Dp))
            }
        }

    if (state.error.isNotBlank()) {
        Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
    }


}
