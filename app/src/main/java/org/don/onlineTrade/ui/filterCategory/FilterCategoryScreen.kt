package org.don.onlineTrade.ui.filterCategory

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
import org.don.onlineTrade.ui.home.ProductItem
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading


@Composable
fun FilterCategoryRoute(
    modifier: Modifier = Modifier,
    list: List<Data>,
    onItemClicked: (Int) -> Unit
) {
    FilterCategoryScreen(
        modifier = modifier,
        onItemClicked = onItemClicked,
        list = list
    )
}


@Composable
fun FilterCategoryScreen(
    modifier: Modifier = Modifier,
    onItemClicked: (Int) -> Unit,
    list: List<Data>
) {

    val isFreeLoading = list.isEmpty()
    if (isFreeLoading) {
        FreeLoading(isFreeLoading)
    }
    if (list.isNotEmpty()) {
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
    }


}