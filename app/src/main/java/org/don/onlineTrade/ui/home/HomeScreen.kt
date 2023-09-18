package org.don.onlineTrade.ui.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.OnlineMarketLoadingWheel


@Composable
fun HomeRoute(
    modifier: Modifier = Modifier
) {
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val state = homeViewModel.state.value
    val products = homeViewModel.collectProducts().collectAsLazyPagingItems()

    HomeScreen(modifier = modifier, state = state, pagingItems = products)
}

@Composable
fun HomeScreen(
    modifier: Modifier,
    state: HomeScreenState,
    pagingItems: LazyPagingItems<Data>
) {
    val isFeedLoading = state.isLoading

    val context = LocalContext.current
    val columnState = rememberLazyGridState()

    Box(modifier = modifier.fillMaxSize()){
        LazyColumn(
            modifier = modifier.fillMaxSize()
        ) {
            item {
                if (state.registerMain != null) {
                    Categories(state.registerMain)
                }
            }

            item {

                if (pagingItems.itemSnapshotList.items.isNotEmpty()){
                    ProductsItemsList(pagingItems)
                }
            }

        }

        if (state.error.isNotBlank()) {
            Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
        }

        AnimatedVisibility(
            visible = isFeedLoading,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> -fullHeight },
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight },
            ) + fadeOut(),
        ) {
            val loadingContentDescription = stringResource(id = R.string.for_you_loading)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                OnlineMarketLoadingWheel(
                    modifier = Modifier
                        .align(Alignment.Center),
                    contentDesc = loadingContentDescription,
                )
            }
        }

    }

}






const val alphaValue = 0.3f
const val alphaValue06 = 0.6f

