package org.don.onlineTrade.ui.detailsPage

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.showProducts.Data
import org.don.onlineTrade.data.remote.models.showProducts.PostParam
import org.don.onlineTrade.ui.add.TextBold16
import org.don.onlineTrade.ui.add.TextNormal16
import org.don.onlineTrade.ui.add.TextThin
import org.don.onlineTrade.ui.home.HomeViewModel
import org.don.onlineTrade.ui.home.PresentProductState
import org.don.onlineTrade.ui.home.ProductItemForDetailsPage
import org.don.onlineTrade.ui.home.ScreenState
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
    navigateBack: () -> Unit,
    goToMapsPage: (lat: Double, lon: Double) -> Unit

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
        onEditClicked = onEditClicked,
        onBackPressed = navigateBack,
        goToMapsPage = goToMapsPage
    )
}

@Composable
fun ProductDetailsScreen(
    mPagerState: ScreenState,
    modifier: Modifier = Modifier,
    state: PresentProductState,
    onSimilarItemClicked: (Int) -> Unit,
    onItemClicked: (Int) -> Unit,
    loadItems: () -> Unit,
    onDeleteClicked: (Int) -> Unit,
    onEditClicked: (Int) -> Unit,
    onBackPressed: () -> Unit,
    goToMapsPage: (lat: Double, lon: Double) -> Unit
) {

    val systemUiController = rememberSystemUiController()
    val isDarkMode = isSystemInDarkTheme()

    DisposableEffect(systemUiController) {
        systemUiController.setStatusBarColor(Color.Transparent, darkIcons = false)
        onDispose {
            systemUiController.setStatusBarColor(
                color = Color.Transparent, darkIcons = isDarkMode
            )
        }
    }
    val isFeedLoading = state.isLoading
    val data = state.registerMain?.data

    val pagerState = rememberPagerState(pageCount = {
        state.registerMain?.data?.images?.size ?: 0
    })

    val context = LocalContext.current
    val scrollState = rememberLazyGridState()
    var showDeleteDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var deletePostId by rememberSaveable {
        mutableIntStateOf(-1)
    }

    val paddingValues = WindowInsets.systemBars.asPaddingValues()
    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        val (column, optionsScreen) = createRefs()
        LazyColumn(
            modifier = modifier
                .padding(bottom = paddingValues.calculateBottomPadding())
                .constrainAs(column) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(optionsScreen.top)
                }
        ) {
            item {
                ImagePager(state, pagerState)
                ItemDescription(data, goToMapsPage)
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
                ContactDetails(data)
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
                TextBold16(
                    title = stringResource(id = R.string.similar_items),
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
            }
        }
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
                callTo((data?.user?.phoneNumber ?: ""), context)
            },
            onSmsClicked = {
                openSmsApp(context, (data?.user?.phoneNumber ?: ""))
            },
            data = data
        )
        TopShadow()

        DetailsToolbar(
            onBackClick = onBackPressed,
            onLikeClicked = onItemClicked,
            data = data
        )
    }
    FreeLoading(
        isFeedLoading = isFeedLoading,
        paddingTop = MaterialTheme.spacing.dimen56Dp
    )

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

    val paddingValues = WindowInsets.systemBars.asPaddingValues()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = paddingValues.calculateBottomPadding())
            .wrapContentHeight()
            .shadow(elevation = 6.dp)
            .background(
                color = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Top
    ) {
        if (data?.user?.id == SharedPref.userId) {
            CircularImage(
                icon = Icons.Filled.Delete,
                onClicked = { onDeleteClicked.invoke(data.id) }
            )
            CircularImage(
                icon = Icons.Filled.Edit,
                onClicked = { onEditClicked.invoke(data.id) }
            )
        }
        CircularImage(
            icon = Icons.Filled.Call,
            onClicked = onCallClicked
        )
        CircularImage(
            icon = Icons.Filled.Sms,
            onClicked = onSmsClicked
        )
    }
}


@Composable
fun CircularImage(
    icon: ImageVector = Icons.Filled.Delete,
    onClicked: () -> Unit
) {
    FloatingActionButton(
        onClick = onClicked,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(0.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null)
    }
}


@Composable
fun ItemDescription(
    data: Data?,
    goToMapsPage: (lat: Double, lon: Double) -> Unit
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
        ProductDescription(data)
        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = MaterialTheme.spacing.dimen10Dp)
        )
        val region = data?.region?.name
        val district = data?.district?.name
        val address = if (region != null && district != null) "$region, $district"
        else if (data?.addressName != null) data.addressName else ""
        DescriptionItems(desc = address,
            onClicked = {
                if (data?.latitude != null) {
                    goToMapsPage.invoke(data.latitude, data.longitude)
                }
            })
        Divider(
            modifier = Modifier
                .padding(vertical = MaterialTheme.spacing.dimen10Dp)
        )
        DescriptionItems(
            imageVector = Icons.Filled.Category,
            title = stringResource(id = R.string.category),
            desc = data?.category?.title ?: "",
            {}
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
    }
}

@Composable
fun ProductDescription(
    data: Data?,
) {
    val category = data?.category
    val params = category?.post_param
    Column {
        TextNormal16(title = data?.title ?: "")
        PriceWrapper(params) { label, unit ->
            TextBold16(title = "$label $unit")
        }
        if (data != null) {
            PostParams(data)
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))
        TextThin(title = data?.description ?: "")
    }
}

@Composable
fun PriceWrapper(
    params: List<PostParam>?,
    content: @Composable (label: String, unit: String) -> Unit
) {
    params?.let { param ->
        val singleParam = param.find { it.code == "price" }
        singleParam?.let {
            if (it.post_value.isNotEmpty()) {
                val label = it.post_value[0].label
                val unit = it.param_unit?.label ?: ""
                content(label, unit)
            }
        }
    }
}

@Composable
fun DescriptionItems(
    imageVector: ImageVector = Icons.Filled.LocationOn,
    title: String = stringResource(id = R.string.address),
    desc: String,
    onClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable {
                onClicked.invoke()
            },
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Image(
            imageVector = imageVector, contentDescription = null,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primaryContainer)
        )

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen12Dp))

        Column(
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            TextThin(title = title)
            TextNormal16(title = desc)
        }
        Spacer(modifier = Modifier.weight(1f))
        Image(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = null)
    }
}


@Composable
fun ContactDetails(
    data: Data?
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
        TextBold16(title = stringResource(id = R.string.contact_details))
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
        DescriptionItems(
            imageVector = Icons.Filled.Person,
            title = stringResource(id = R.string.name),
            desc = "${data?.user?.name}",
            onClicked = {}
        )
        Divider(
            modifier = Modifier
                .padding(vertical = MaterialTheme.spacing.dimen10Dp)
        )
        val context = LocalContext.current
        DescriptionItems(
            imageVector = Icons.Filled.Phone,
            title = stringResource(id = R.string.name),
            desc = data?.user?.phoneNumber ?: "",
            onClicked = {
                callTo((data?.user?.phoneNumber ?: ""), context)
            }
        )
    }

}

