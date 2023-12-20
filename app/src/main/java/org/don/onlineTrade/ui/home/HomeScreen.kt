package org.don.onlineTrade.ui.home

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.don.onlineTrade.R
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

    LaunchedEffect(key1 = viewModel) {
        viewModel.loadNextItems()
        viewModel.getAllCategories()
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.dimen8Dp)
        ) {
            if (state.registerMain != null) {
                item {
                    Text(
                        text = stringResource(id = R.string.category),
                        style = TextStyle.Default,
                        modifier = Modifier.padding(
                            start = MaterialTheme.spacing.dimen8Dp,
                            bottom = MaterialTheme.spacing.dimen8Dp
                        ),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = MaterialTheme.spacing.dimen16Sp
                    )
                }
                item(key = "Categories", span = { GridItemSpan(2) }) {
                    Categories(
                        state.registerMain,
                        navigateToCategory = navigateToCategory
                    )
                }
            }
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = stringResource(id = R.string.all_posts),
                    style = TextStyle.Default,
                    modifier = Modifier.padding(
                        start = MaterialTheme.spacing.dimen8Dp,
                        top = MaterialTheme.spacing.dimen8Dp
                    ),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = MaterialTheme.spacing.dimen16Sp
                )
            }
            items(count = pagerState.items.size) { i ->
                val item = pagerState.items[i]
                LaunchedEffect(scrollState) {
                    if (i >= pagerState.items.size - 1 && !pagerState.endReached && !pagerState.isLoading) {
                        viewModel.loadNextItems()
                    }
                }

                ProductItem(item, onItemClicked = navigateToProduct)
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
        FreeLoading(isFeedLoading = isFeedLoading)
    }


    if (state.error.isNotBlank()) {
        Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        modifier = Modifier,
        navigateToProduct = {},
        navigateToCategory = {}
    )
}