package org.don.onlineTrade.ui.filterCategory

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
import org.don.onlineTrade.ui.home.HomeViewModel
import org.don.onlineTrade.ui.home.ProductItem
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading


@Composable
fun FilterCategoryRoute(
    modifier: Modifier = Modifier,
    onItemClicked: (Int) -> Unit,
    categoryId: Int?
) {
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val products = homeViewModel.collectProducts(
        categoryId = categoryId
    ).collectAsLazyPagingItems()


    FilterCategoryScreen(
        modifier = modifier,
        onItemClicked = onItemClicked,
        pagingItems = products
    )
}


@Composable
fun FilterCategoryScreen(
    modifier: Modifier = Modifier,
    onItemClicked: (Int) -> Unit,
    pagingItems: LazyPagingItems<Data>
) {
    Log.d("TAG", "FilterCategoryScreendwaddaw1111212")
    val list = pagingItems.itemSnapshotList.items



    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier
                .padding(end = MaterialTheme.spacing.dimen16Dp)
                .fillMaxSize(),
        ) {
            items(list) {
                ProductItem(
                    data = it,
                    onItemClicked = onItemClicked
                )
            }
        }
        FreeLoading(list.isEmpty())
    }

}