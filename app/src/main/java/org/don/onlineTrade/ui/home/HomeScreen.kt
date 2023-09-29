package org.don.onlineTrade.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
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

    HomeScreen(modifier = modifier, state = state, pagingItems = products,
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

    Box(modifier = modifier.fillMaxSize()){
        LazyColumn(
            modifier = modifier.fillMaxSize()
        ) {
            item {
                if (state.registerMain != null) {
                    Categories(state.registerMain,
                        navigateToCategory = navigateToCategory)
                }
            }

            item {
                if (pagingItems.itemSnapshotList.items.isNotEmpty()){
                    ProductsItemsList(pagingItems,
                        onItemClicked = navigateToProduct)
                }
            }

        }

        if (state.error.isNotBlank()) {
            Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
        }

        FreeLoading(isFeedLoading = isFeedLoading)

    }

}






const val alphaValue = 0.3f
const val alphaValue06 = 0.6f

