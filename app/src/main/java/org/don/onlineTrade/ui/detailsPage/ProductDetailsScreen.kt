package org.don.onlineTrade.ui.detailsPage

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.getPublicProducts.Content
import org.don.onlineTrade.data.remote.models.showProducts.PostDetailsModel
import org.don.onlineTrade.ui.add.ProductTitle
import org.don.onlineTrade.ui.add.TextBold
import org.don.onlineTrade.ui.add.TextThin
import org.don.onlineTrade.ui.home.HomeScreenState
import org.don.onlineTrade.ui.home.HomeViewModel
import org.don.onlineTrade.ui.home.PresentProductState
import org.don.onlineTrade.ui.home.ProductItem
import org.don.onlineTrade.ui.home.ProductItemForDetailsPage
import org.don.onlineTrade.ui.home.ScreenState
import org.don.onlineTrade.ui.home.TOKEN
import org.don.onlineTrade.ui.home.getCurrency
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading
import org.don.onlineTrade.utils.SharedPref


@Composable
fun ProductDetailsRoute(
    productId: Int,
    onSimilarItemClicked: (Int) -> Unit
) {
    val detailsViewModel = hiltViewModel<PresentViewModel>()
    val homeViewMode = hiltViewModel<HomeViewModel>()
    val pagerState = homeViewMode.pagerState

    LaunchedEffect(key1 = "hello") {
        homeViewMode.loadNextItems()
        detailsViewModel.getProductDetail(
            id = productId,
            language = SharedPref.language,
            token = SharedPref.deviceToken
        )
    }

    val state = detailsViewModel.state.value
    ProductDetailsScreen(
        pagerState,
        state = state,
        onSimilarItemClicked = onSimilarItemClicked,
        onItemClicked = {
            detailsViewModel.likePost(it)
        },
        loadItems = {
            homeViewMode.loadNextItems()
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductDetailsScreen(
    mPagerState: ScreenState,
    modifier: Modifier = Modifier,
    state: PresentProductState,
    onSimilarItemClicked: (Int) -> Unit,
    onItemClicked: (Int) -> Unit,
    loadItems: () -> Unit
) {

    val isFeedLoading = state.isLoading

    val pagerState = rememberPagerState(pageCount = {
        state.registerMain?.data?.images?.size ?: 0
    })

    var loadedData by remember {
        mutableStateOf(PresentProductState())
    }
    loadedData = state
    val scrollState = rememberLazyGridState()

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(
                    rememberScrollState()
                )
        ) {
            ImagePager(loadedData, pagerState)

            ItemDescription(loadedData, onLikeClicked = onItemClicked)

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
            ContactDetails(loadedData.registerMain)

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
            TextBold(
                title = stringResource(id = R.string.similar_items),
                modifier = Modifier
                    .padding(start = MaterialTheme.spacing.dimen16Dp)
            )


            LazyRow(modifier = Modifier.wrapContentHeight()){
                items(count = mPagerState.items.size) { i ->
                    val item = mPagerState.items[i]
                    LaunchedEffect(scrollState) {
                        if (i >= mPagerState.items.size - 1 && !mPagerState.endReached && !mPagerState.isLoading) {
                            loadItems.invoke()
                        }
                    }
                    ProductItemForDetailsPage(
                        data = item,
                        onItemClicked = onItemClicked
                    )
                    if (mPagerState.items.lastIndex == i){
                        Spacer(modifier = modifier.width(16.dp))
                    }
                }
            }

            Spacer(modifier = modifier.height(24.dp))
        }

    }
    FreeLoading(isFeedLoading = isFeedLoading)
}



@Composable
fun ItemDescription(
    state: PresentProductState,
    onLikeClicked: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(
                vertical = MaterialTheme.spacing.dimen12Dp,
                horizontal = MaterialTheme.spacing.dimen16Dp
            ),
    ) {
        ProductDescription(state.registerMain, onLikeClicked)
        Divider(
            modifier = Modifier
                .padding(vertical = MaterialTheme.spacing.dimen10Dp)
        )
        val region = state.registerMain?.data?.region?.name
        val district = state.registerMain?.data?.district?.name
        DescriptionItems(desc = "$region, $district")
        Divider(
            modifier = Modifier
                .padding(vertical = MaterialTheme.spacing.dimen10Dp)
        )
        DescriptionItems(
            imageVector = Icons.Filled.Category,
            title = stringResource(id = R.string.category),
            desc = state.registerMain?.data?.category?.title ?: ""
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
    }
}

@Composable
fun ProductDescription(
    item: PostDetailsModel?,
    onLikeClicked: (Int) -> Unit
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
                ProductTitle(title = item?.data?.title ?: "")
                TextBold(title = "${item?.data?.price} ${getCurrency(currencyId = item?.data?.currency_id ?: 0)}")
            }

            IconButton(onClick = {
                item?.data?.id?.let {
                    onLikeClicked(it)
                }
            }) {

                if (item?.data?.isLiked == true) {
                    Image(
                        painter = painterResource(id = R.drawable.ph_heart_fill),
                        contentDescription = null
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.solar_heart_outline),
                        contentDescription = null
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))
        TextThin(title = item?.data?.description ?: "")
    }
}

@Composable
fun DescriptionItems(
    imageVector: ImageVector = Icons.Filled.AddLocation,
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
    state: PostDetailsModel?
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
            desc = "${state?.data?.user?.lastName} ${state?.data?.user?.firstName}"
        )
        Divider(
            modifier = Modifier
                .padding(vertical = MaterialTheme.spacing.dimen10Dp)
        )
        DescriptionItems(
            imageVector = Icons.Filled.Phone,
            title = stringResource(id = R.string.name),
            desc = state?.data?.user?.phoneNumber ?: ""
        )


    }


}
