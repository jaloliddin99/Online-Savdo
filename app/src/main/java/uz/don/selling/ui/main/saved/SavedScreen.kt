package uz.don.selling.ui.main.saved

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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uz.don.selling.R
import uz.don.selling.ui.TopAppBar
import uz.don.selling.ui.main.home.ProductItem
import uz.don.selling.ui.main.home.homeItems.ShimmerProductGrid
import uz.don.selling.ui.theme.spacing


@Composable
fun SavedRoute(
    modifier: Modifier = Modifier,
    navigateToProduct: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = stringResource(R.string.saved_page),
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )
        SavedScreen(modifier = Modifier.weight(1f), navigateToProduct = navigateToProduct)
    }
}

@Composable
fun SavedScreen(
    modifier: Modifier,
    navigateToProduct: (Int) -> Unit
) {

    val viewModel = hiltViewModel<LikedViewModel>()

    val pagerState = viewModel.pagerState
    val scrollState = rememberLazyGridState()

    LaunchedEffect(key1 = viewModel) {
        viewModel.loadNextItems()
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2)
        ) {
            if (pagerState.isLoading && pagerState.items.isEmpty() && pagerState.page == 0) {
                item(span = { GridItemSpan(2) }) {
                    ShimmerProductGrid()
                }
            } else {
                items(count = pagerState.items.size) { i ->
                    val item = pagerState.items[i]
                    LaunchedEffect(scrollState) {
                        if (i >= pagerState.items.size - 1 && !pagerState.endReached && !pagerState.isLoading) {
                            viewModel.loadNextItems()
                        }
                    }
                    ProductItem(
                        item, onItemClicked = {
                            navigateToProduct.invoke(it)
                        }, isLiked = true,
                        onItemLongLicked = {}
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
        }
    }

}

