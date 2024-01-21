package org.don.onlineTrade.ui.detailsPage

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.showProducts.Data
import org.don.onlineTrade.data.remote.models.showProducts.PostDetailsModel
import org.don.onlineTrade.data.remote.models.showProducts.PostParam
import org.don.onlineTrade.ui.add.ProductTitle
import org.don.onlineTrade.ui.add.TextBold
import org.don.onlineTrade.ui.add.TextThin
import org.don.onlineTrade.ui.home.HomeViewModel
import org.don.onlineTrade.ui.home.PresentProductState
import org.don.onlineTrade.ui.home.ProductItemForDetailsPage
import org.don.onlineTrade.ui.home.ScreenState
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading
import org.don.onlineTrade.utils.SharedPref
import org.don.onlineTrade.utils.callTo
import org.don.onlineTrade.utils.openSmsApp


@Composable
fun ProductDetailsRoute(
    productId: Int,
    onSimilarItemClicked: (Int) -> Unit,
    onEditClicked: (Int) -> Unit,
    navigateBack: () -> Unit
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
    if (state.delete != null) {
        navigateBack.invoke()
    }

    ProductDetailsScreen(
        pagerState,
        state = state,
        onSimilarItemClicked = onSimilarItemClicked,
        onItemClicked = {
            detailsViewModel.likePost(it)
        },
        loadItems = {
            homeViewMode.loadNextItems()
        },
        onDeleteClicked = {
            detailsViewModel.deletePost(it)
        },
        onEditClicked = onEditClicked
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
    loadItems: () -> Unit,
    onDeleteClicked: (Int) -> Unit,
    onEditClicked: (Int) -> Unit
) {

    val systemUiController = rememberSystemUiController()
    val isDarkMode = isSystemInDarkTheme()

    DisposableEffect(key1 = true) {
        systemUiController.setStatusBarColor(Color.Transparent, darkIcons = false)
        onDispose {
            systemUiController.setStatusBarColor(Color.Transparent, darkIcons = !isDarkMode)
        }
    }
    val isFeedLoading = state.isLoading

    val pagerState = rememberPagerState(pageCount = {
        state.registerMain?.data?.images?.size ?: 0
    })

    val context = LocalContext.current
    var loadedData by remember {
        mutableStateOf(PresentProductState())
    }
    loadedData = state
    val scrollState = rememberLazyGridState()
    var showDeleteDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var deletePostId by rememberSaveable {
        mutableIntStateOf(-1)
    }


    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
    ) {
        val (column, optionsScreen) = createRefs()
        Column(
            modifier = modifier
                .fillMaxWidth()
                .constrainAs(column) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(optionsScreen.top)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
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


            LazyRow(modifier = Modifier.wrapContentHeight()) {
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
                    if (mPagerState.items.lastIndex == i) {
                        Spacer(modifier = modifier.width(16.dp))
                    }
                }
            }

            Spacer(modifier = modifier.height(24.dp))
            Spacer(modifier = Modifier.weight(1f))
        }
        val user = state.registerMain?.data
        OptionsScreen(
            modifier = Modifier
                .constrainAs(optionsScreen) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            onDeleteClicked = {
                showDeleteDialog = true
                deletePostId = it
            },
            onEditClicked = onEditClicked,
            onCallClicked = {
                callTo((user?.user?.phoneNumber ?: ""), context)
            },
            onSmsClicked = {
                openSmsApp(context, (user?.user?.phoneNumber ?: ""))
            },
            data = user
        )
        TopShadow()

        DetailsToolbar(onBackClick = {})
    }
    FreeLoading(isFeedLoading = isFeedLoading)

    if (showDeleteDialog) {
        DeletePostAlert(
            onDismiss = {
                showDeleteDialog = false
            },
            onDeleteConfirm = {
                showDeleteDialog = false
                onDeleteClicked.invoke(deletePostId)
            }
        )
    }

}


@Composable
fun OptionsScreen(
    modifier: Modifier,
    onDeleteClicked: (Int) -> Unit,
    onEditClicked: (Int) -> Unit,
    onCallClicked: () -> Unit,
    onSmsClicked: () -> Unit,
    data: Data?
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .shadow(elevation = 6.dp)
            .background(
                color = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top
    ) {
        if (data?.user?.id == SharedPref.userId) {
            CircularImage(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Delete,
                title = R.string.delete_post,
                onClicked = { onDeleteClicked.invoke(data.id) }
            )
            CircularImage(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Edit,
                title = R.string.edit_post,
                onClicked = { onEditClicked.invoke(data.id) }
            )
        }
        CircularImage(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Call,
            title = R.string.call,
            onClicked = onCallClicked
        )
        CircularImage(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Sms,
            title = R.string.send_msg,
            onClicked = onSmsClicked
        )
    }
}



@Composable
fun CircularImage(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Filled.Delete,
    @StringRes title: Int,
    onClicked: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        FilledIconButton(
            modifier = Modifier
                .width(56.dp)
                .height(56.dp)
                .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape),
            onClick = onClicked
        ) {
            Icon(imageVector = icon, contentDescription = null)
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))

        Text(
            text = stringResource(id = title),
            fontWeight = FontWeight.Normal,
            fontFamily = robotoFontFamily,
            fontSize = MaterialTheme.spacing.dimen12Sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
fun ItemDescription(
    state: PresentProductState,
    onLikeClicked: (Int) -> Unit
) {
    val data = state.registerMain?.data
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
        val region = data?.region?.name
        val district = data?.district?.name
        val  address = if (region != null && district != null) "$region, $district"
        else if (data?.addressName != null) data.addressName else ""
        DescriptionItems(desc = address)
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
    val data = item?.data
    val category = data?.category
    val params = category?.post_param
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                ProductTitle(title = data?.title ?: "")
                PriceWrapper(params){
                    TextBold(title = it)
                }
            }

            IconButton(onClick = {
                data?.id?.let {
                    onLikeClicked(it)
                }
            }) {

                if (data?.isLiked == true) {
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
        TextThin(title = data?.description ?: "")
    }
}

@Composable
fun PriceWrapper(
    params: List<PostParam>?,
    content: @Composable (label:String) -> Unit
) {
    params?.let {param ->
        val singleParam = param.find { it.code == "price" }
        singleParam?.let {
            if (it.post_value.isNotEmpty()){
                val label = it.post_value[0].label
                content(label)
            }
        }
    }
}

@Composable
fun DescriptionItems(
    imageVector: ImageVector = Icons.Filled.LocationOn,
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
            desc = "${state?.data?.user?.name}"
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
