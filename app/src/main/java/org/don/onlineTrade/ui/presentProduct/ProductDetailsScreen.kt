package org.don.onlineTrade.ui.presentProduct

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
import org.don.onlineTrade.data.remote.models.showProducts.ShowProductModel
import org.don.onlineTrade.ui.add.ProductTitle
import org.don.onlineTrade.ui.add.TextBold
import org.don.onlineTrade.ui.add.TextThin
import org.don.onlineTrade.ui.home.HomeViewModel
import org.don.onlineTrade.ui.home.PresentProductState
import org.don.onlineTrade.ui.home.ProductItem
import org.don.onlineTrade.ui.home.TOKEN
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading
import org.don.onlineTrade.utils.SharedPref


@Composable
fun ProductDetailsRoute(
    productId: Int,
    onSimilarItemClicked: (Int) -> Unit
) {
    val detailsViewModel = hiltViewModel<PresentViewModel>()



    LaunchedEffect(key1 = "hello") {
        detailsViewModel.getProductDetail(
            id = productId,
            language = SharedPref.language,
            token = TOKEN
        )
    }

    val state = detailsViewModel.state.value
    ProductDetailsScreen(
        state = state,
        onSimilarItemClicked = onSimilarItemClicked,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductDetailsScreen(
    modifier: Modifier = Modifier,
    state: PresentProductState,
    onSimilarItemClicked: (Int) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {

    val isFeedLoading = state.isLoading
    FreeLoading(isFeedLoading = isFeedLoading)
    if (state.registerMain != null) {
        val pagingItems = homeViewModel.collectProducts(
            categoryId = state.registerMain.category.id
        ).collectAsLazyPagingItems()
        val pagerState = rememberPagerState(pageCount = {
            state.registerMain.images.size
        })

        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            FreeLoading(isFeedLoading = isFeedLoading)
            LazyColumn {
                item {
                    ImagePager(state, pagerState)
                }

                item {
                    ItemDescription(state)
                }

                item {
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
                    ContactDetails(state.registerMain)
                }

                item {
                    val itemSize: Dp = (LocalConfiguration.current.screenWidthDp.dp / 2) - 24.dp
                    val list = pagingItems.itemSnapshotList.items
                    if (list.isNotEmpty()) {
                        SimilarContents(
                            list,
                            itemSize,
                            onSimilarItemClicked
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SimilarContents(
    list: List<Data>,
    itemSize: Dp,
    onItemClicked: (Int) -> Unit
) {
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
    TextBold(
        title = stringResource(id = R.string.similar_items)
        , modifier = Modifier
            .padding(start = MaterialTheme.spacing.dimen16Dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        list.forEachIndexed { index, data ->
            ProductItem(
                data = data,
                onItemClicked = onItemClicked,
                paddingValues = PaddingValues(
                    start = MaterialTheme.spacing.dimen16Dp,
                    top = MaterialTheme.spacing.dimen16Dp,
                )
            )
            if (index == list.lastIndex){
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen16Dp))
            }
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
}



@Composable
fun ItemDescription(state: PresentProductState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(
                vertical = MaterialTheme.spacing.dimen12Dp,
                horizontal = MaterialTheme.spacing.dimen16Dp
            ),
    ) {
        ProductDescription(state.registerMain!!)
        Divider(
            modifier = Modifier
                .padding(vertical = MaterialTheme.spacing.dimen10Dp)
        )
        DescriptionItems(desc = state.registerMain.region.title)
        Divider(
            modifier = Modifier
                .padding(vertical = MaterialTheme.spacing.dimen10Dp)
        )
        DescriptionItems(
            imageVector = Icons.Filled.Category,
            title = stringResource(id = R.string.category),
            desc = state.registerMain.category.title
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
    }
}

@Composable
fun ProductDescription(
    item: ShowProductModel
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                ProductTitle(title = item.title)
                TextBold(title = item.price)
            }

            Image(
                imageVector = Icons.Filled.HeartBroken, contentDescription = null,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface)
            )
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))
        TextThin(title = item.description)
    }
}

@Composable
fun DescriptionItems(
    imageVector: ImageVector = Icons.Filled.MyLocation,
    title: String = stringResource(id = R.string.location),
    desc: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            imageVector = imageVector, contentDescription = null,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
        )

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen12Dp))

        Column(
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            TextThin(title = title)
            ProductTitle(title = desc)
        }
        Spacer(modifier = Modifier.weight(1f))
        Image(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = null)
    }
}


@Composable
fun ContactDetails(
    state: ShowProductModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(
                vertical = MaterialTheme.spacing.dimen12Dp,
                horizontal = MaterialTheme.spacing.dimen16Dp
            )
    ) {
        TextBold(title = stringResource(id = R.string.contact_details))
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
        DescriptionItems(
            imageVector = Icons.Filled.Person,
            title = stringResource(id = R.string.name),
            desc = state.title
        )
        Divider(
            modifier = Modifier
                .padding(vertical = MaterialTheme.spacing.dimen10Dp)
        )
        if (state.user.phone_number != null) {
            DescriptionItems(
                imageVector = Icons.Filled.Phone,
                title = stringResource(id = R.string.name),
                desc = state.user.phone_number.toString()
            )
        }
    }


}
