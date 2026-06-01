package uz.promo.selling.ui.filterCategory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.LoadState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import uz.promo.selling.R
import uz.promo.selling.ui.TopAppBar
import uz.promo.selling.ui.main.home.ProductItem
import uz.promo.selling.ui.theme.spacing
import uz.promo.selling.utils.FreeLoading


@Composable
fun FilterCategoryRoute(
    modifier: Modifier = Modifier,
    onItemClicked: (Int) -> Unit,
    categoryId: Int?,
    onBackClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = stringResource(R.string.categories),
            navigationIcon = Icons.Filled.ArrowBack,
            onNavigationClick = onBackClick,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )
        FilterCategoryScreen(
            modifier = modifier.weight(1f),
            onItemClicked = onItemClicked,
            categoryId = categoryId
        )
    }
}


@Composable
fun FilterCategoryScreen(
    modifier: Modifier = Modifier,
    onItemClicked: (Int) -> Unit,
    categoryId: Int?
) {

    val viewModel = hiltViewModel<FilterCategoryViewModel>()
    val products = viewModel.products.collectAsLazyPagingItems()
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier
                .fillMaxSize(),
        ) {
            items(
                count = products.itemCount,
                key = { products[it]?.id ?: it }
            ) { i ->
                products[i]?.let { item ->
                    ProductItem(item, onItemClicked = onItemClicked, onItemLongLicked = {})
                }
            }
            if (products.loadState.append is LoadState.Loading) {
                item(span = { GridItemSpan(2) }) {
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
        if (products.loadState.refresh is LoadState.NotLoading && products.itemCount == 0) {
            ComposeLottieAnimation(Modifier)
        }
        FreeLoading(products.loadState.refresh is LoadState.Loading)
    }

}

@Composable
fun ComposeLottieAnimation(modifier: Modifier) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.not_order))
    Box {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                modifier = modifier
                    .width(150.dp)
                    .height(150.dp),
                composition = composition,
                iterations = LottieConstants.IterateForever,
            )
            Text(
                text = stringResource(id = R.string.nothing_found_for_for_search),
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}


@Preview
@Composable
fun FilterCategoryRoutePreview(modifier: Modifier = Modifier) {
    MaterialTheme {
        FilterCategoryRoute(
            onItemClicked = {},
            categoryId = 1,
            onBackClick = {}
        )
    }
}