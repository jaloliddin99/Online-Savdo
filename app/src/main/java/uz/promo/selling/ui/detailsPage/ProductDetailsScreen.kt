package uz.promo.selling.ui.detailsPage

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import uz.promo.selling.BuildConfig
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.showProducts.PostDetailsData
import uz.promo.selling.data.remote.models.showProducts.PostParam
import uz.promo.selling.ui.main.home.PresentProductState
import uz.promo.selling.ui.main.home.ProductItemForDetailsPage
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.ui.chat.ChatStartViewModel
import uz.promo.selling.utils.SharedPref
import android.widget.Toast
import uz.promo.selling.utils.callTo
import uz.promo.selling.utils.openSmsApp
import uz.promo.selling.utils.stripHtmlBreaks
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


@Composable
fun ProductDetailsRoute(
    productId: Int,
    onSimilarItemClicked: (Int) -> Unit,
    onEditClicked: (Int) -> Unit,
    navigateBack: () -> Unit,
    goToMapsPage: (lat: Double, lon: Double) -> Unit,
    onLoginRequired: () -> Unit = {},
    navigateToChat: (Long) -> Unit = {},
    onSellerClicked: (Int) -> Unit = {},
    imageSharedModifier: Modifier = Modifier
) {
    val detailsViewModel = hiltViewModel<PresentViewModel>()
    val chatStartViewModel = hiltViewModel<ChatStartViewModel>()
    val routeContext = LocalContext.current
    val similarProducts = detailsViewModel.similarProducts.collectAsLazyPagingItems()

    // Reload whenever the screen comes back to the foreground, so returning from
    // the edit wizard shows the saved changes instead of the stale copy.
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    LaunchedEffect(productId, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.RESUMED) {
            detailsViewModel.getProductDetail(
                id = productId,
                language = SharedPref.language,
            )
        }
    }

    val state = detailsViewModel.state.value
    // Must be a side effect: `state.delete` stays non-null after a successful delete,
    // so navigating straight from composition popped the back stack on every
    // recomposition until it was empty.
    LaunchedEffect(state.delete) {
        if (state.delete != null) navigateBack.invoke()
    }

    ProductDetailsScreen(
        similarProducts = similarProducts,
        modifier = Modifier.fillMaxSize(),
        state = state,
        imageSharedModifier = imageSharedModifier,
        onSimilarItemClicked = onSimilarItemClicked,
        onItemClicked = {
            if (SharedPref.deviceToken.isEmpty()) {
                onLoginRequired()
            } else {
                detailsViewModel.likePost(it)
            }
        },
        onDeleteClicked = {
            detailsViewModel.deletePost(it)
        },
        onEditClicked = onEditClicked,
        onBackPressed = navigateBack,
        goToMapsPage = goToMapsPage,
        onMessageClicked = {
            val pid = state.registerMain?.id
            if (pid != null) {
                if (SharedPref.deviceToken.isEmpty()) {
                    onLoginRequired()
                } else {
                    chatStartViewModel.start(
                        postId = pid,
                        onSuccess = { navigateToChat(it) },
                        onError = {
                            Toast.makeText(
                                routeContext,
                                routeContext.getString(R.string.chat_self_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        },
        onSellerClicked = onSellerClicked
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductDetailsScreen(
    similarProducts: androidx.paging.compose.LazyPagingItems<uz.promo.selling.data.remote.models.getPublicProducts.Content>,
    modifier: Modifier = Modifier,
    state: PresentProductState,
    onSimilarItemClicked: (Int) -> Unit,
    onItemClicked: (Int) -> Unit,
    onDeleteClicked: (Int) -> Unit,
    onEditClicked: (Int) -> Unit,
    onBackPressed: () -> Unit,
    goToMapsPage: (lat: Double, lon: Double) -> Unit,
    onMessageClicked: () -> Unit = {},
    onSellerClicked: (Int) -> Unit = {},
    imageSharedModifier: Modifier = Modifier
) {

    val isFeedLoading = state.isLoading
    val data = state.registerMain
    // The bottom action bar only shows for the post owner; only reserve space for
    // it in that case so other viewers don't get a large empty gap at the bottom.
    val isOwner = data?.user?.id == SharedPref.userId

    val pagerState = rememberPagerState(pageCount = {
        state.registerMain?.images?.size ?: 0
    })

    val context = LocalContext.current
    var showDeleteDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var deletePostId by rememberSaveable {
        mutableIntStateOf(-1)
    }

    if (isFeedLoading && data == null) {
        ShimmerDetailsContent()
    } else {
        // Backdrop for the glass toolbar buttons and floating bottom bars.
        val hazeState = remember { HazeState() }
        val listState = rememberLazyListState()
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(state = hazeState),
                // Content scrolls under the floating action bar; keep the tail reachable.
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 108.dp)
            ) {
                // Hero image — rounded sheet edge + parallax: the photo scrolls at
                // half speed behind the content, like a depth layer.
                item(key = "image_pager") {
                    Box(
                        modifier = Modifier.clip(
                            RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                        )
                    ) {
                        Box(
                            modifier = Modifier.graphicsLayer {
                                val scrolled = if (listState.firstVisibleItemIndex == 0)
                                    listState.firstVisibleItemScrollOffset.toFloat() else 0f
                                translationY = scrolled * 0.5f
                            }
                        ) {
                            ImagePager(state, pagerState, imageSharedModifier)
                        }
                    }
                }

                // Title + Price + Meta
                item(key = "header") {
                    ProductHeader(data, onItemClicked)
                }

                // Description
                if (!data?.description.isNullOrBlank()) {
                    item(key = "description") {
                        DescriptionSection(data?.description ?: "")
                    }
                }

                // Characteristics
                if (data != null) {
                    val params = data.category.post_param.filter { it.type != "price" }
                    if (params.isNotEmpty()) {
                        item(key = "characteristics") {
                            CharacteristicsSection(data)
                        }
                    }
                }

                // Location
                item(key = "location") {
                    LocationSection(data, goToMapsPage)
                }

                // Seller
                item(key = "seller") {
                    SellerSection(data, context, onMessageClicked, onSellerClicked)
                }

                // Similar items
                if (similarProducts.itemCount > 0) {
                    item(key = "similar_header") {
                        SectionHeader(
                            title = stringResource(id = R.string.similar_items),
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
                        )
                    }

                    item(key = "similar_items") {
                        LazyRow(
                            modifier = Modifier.wrapContentHeight(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                        ) {
                            items(count = similarProducts.itemCount) { i ->
                                similarProducts[i]?.let { item ->
                                    ProductItemForDetailsPage(
                                        data = item,
                                        onItemClicked = onSimilarItemClicked
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom spacing — small breathing room + system nav bar inset.
                item(key = "bottom_spacer") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsBottomHeight(WindowInsets.navigationBars)
                    )
                }
            }

            // Floating glass owner action bar.
            OptionsScreen(
                modifier = Modifier.align(Alignment.BottomCenter),
                hazeState = hazeState,
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

            // Buyer bar: price + primary Call CTA (non-owners only), floating glass,
            // springing up from the bottom on first open.
            if (data != null && !isOwner) {
                val introState = remember {
                    androidx.compose.animation.core.MutableTransitionState(false)
                        .apply { targetState = true }
                }
                AnimatedVisibility(
                    visibleState = introState,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    enter = slideInVertically(
                        animationSpec = spring(
                            dampingRatio = 0.8f,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) { it } + fadeIn()
                ) {
                    BuyerActionBar(
                        modifier = Modifier,
                        hazeState = hazeState,
                        data = data,
                        onCallClicked = { callTo(data.user.phoneNumber ?: "", context) },
                        onMessageClicked = onMessageClicked
                    )
                }
            }

            // Toolbar overlay
            TopShadow()
            DetailsToolbar(
                hazeState = hazeState,
                onBackClick = onBackPressed,
                onLikeClicked = onItemClicked,
                onShareClick = {
                    data?.let {
                        val share = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                android.content.Intent.EXTRA_TEXT,
                                "${it.title}\nhttps://selling.uz/post/${it.id}"
                            )
                        }
                        context.startActivity(android.content.Intent.createChooser(share, null))
                    }
                },
                data = data
            )
        }
    }

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

// ── Section Header ──────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        fontFamily = robotoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

// ── Product Header (title, price, meta row) ─────────────────────

@Composable
private fun ProductHeader(data: PostDetailsData?, onLikeClicked: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(top = 12.dp)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(20.dp), clip = false)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // Sold / expired banner
        if (data?.status == 3 || data?.status == 4) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (data.status == 3) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (data.status == 3) Icons.Outlined.CheckCircle
                    else Icons.Outlined.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(
                        if (data.status == 3) R.string.sold_notice else R.string.expired_notice
                    ),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Title
        Text(
            text = data?.title ?: "",
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        // Price, with the pre-reduction figure struck through beside it when the
        // seller has recently lowered it.
        PriceWrapper(data?.category?.post_param) { label, unit ->
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(top = 6.dp)
            ) {
                Text(
                    text = formatPrice(label, unit),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                data?.previousPrice?.let { old ->
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatPrice(
                            old.toPlainPriceLabel(),
                            data.previousPriceCurrency ?: unit
                        ),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 17.sp,
                        textDecoration = TextDecoration.LineThrough,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Meta chips: date, likes, views, category — wrap instead of overflowing.
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetaChip(
                icon = Icons.Outlined.AccessTime,
                text = formatTimeAgo(LocalContext.current, data?.createdDate)
            )
            MetaChip(
                icon = if (data?.isLiked == true) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                text = "${data?.likes ?: 0} ${stringResource(R.string.likePulural)}",
                iconTint = if (data?.isLiked == true) MaterialTheme.colorScheme.error else null,
                onClick = { data?.id?.let { onLikeClicked(it) } }
            )
            MetaChip(
                icon = Icons.Outlined.Visibility,
                text = "${data?.viewCount ?: 0}"
            )
            MetaChip(
                icon = Icons.Outlined.Category,
                text = data?.category?.title ?: ""
            )
        }
    }
}

@Composable
private fun MetaChip(
    icon: ImageVector,
    text: String,
    iconTint: androidx.compose.ui.graphics.Color? = null,
    onClick: (() -> Unit)? = null
) {
    val contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = iconTint ?: contentColor
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = text,
            fontFamily = robotoFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ── Description Section ─────────────────────────────────────────

@Composable
private fun DescriptionSection(description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(top = 12.dp)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(20.dp), clip = false)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        SectionHeader(title = stringResource(id = R.string.description))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stripHtmlBreaks(description),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            lineHeight = 22.sp
        )
    }
}

// ── Characteristics Section (grid layout) ───────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CharacteristicsSection(data: PostDetailsData) {
    val regularParams = data.category.post_param
        .filter { it.type != "price" && it.type != "multichoice" }
    val multiChoiceParams = data.category.post_param
        .filter { it.type == "multichoice" }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(top = 12.dp)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(20.dp), clip = false)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        SectionHeader(title = stringResource(id = R.string.characteristics))
        Spacer(modifier = Modifier.height(12.dp))

        // Grid of regular params (2 columns)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 2
        ) {
            regularParams.forEach { param ->
                ParamCard(
                    param = param,
                    modifier = Modifier.weight(1f)
                )
            }
            // Add empty spacer if odd count to keep grid aligned
            if (regularParams.size % 2 != 0) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // Multichoice params as chips
        multiChoiceParams.forEach { param ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = param.label,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                param.post_value.forEach { value ->
                    Text(
                        text = value.label,
                        fontFamily = robotoFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ParamCard(param: PostParam, modifier: Modifier = Modifier) {
    val value = param.post_value.firstOrNull()?.label ?: ""
    val unit = param.param_unit?.label ?: ""
    val displayValue = if (unit.isNotEmpty()) "$value $unit" else value

    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
    ) {
        Text(
            text = param.label,
            fontFamily = robotoFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = displayValue,
            fontFamily = robotoFontFamily,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ── Location Section ────────────────────────────────────────────

@Composable
private fun LocationSection(
    data: PostDetailsData?,
    goToMapsPage: (lat: Double, lon: Double) -> Unit
) {
    val lat = data?.latitude
    val lon = data?.longitude

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(top = 12.dp)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(20.dp), clip = false)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        SectionHeader(title = stringResource(id = R.string.address))
        Spacer(modifier = Modifier.height(10.dp))

        // Static (lite-mode) map preview with a pin overlaid in the center;
        // tapping anywhere opens the full interactive map screen.
        if (lat != null && lon != null) {
            val cameraPositionState = com.google.maps.android.compose.rememberCameraPositionState(
                key = "$lat,$lon"
            ) {
                position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
                    com.google.android.gms.maps.model.LatLng(lat, lon), 14.5f
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                com.google.maps.android.compose.GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    googleMapOptionsFactory = {
                        com.google.android.gms.maps.GoogleMapOptions().liteMode(true)
                    },
                    uiSettings = com.google.maps.android.compose.MapUiSettings(
                        zoomControlsEnabled = false,
                        mapToolbarEnabled = false,
                        compassEnabled = false
                    )
                )
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.Center)
                        // Lift so the pin's tip points at the actual location.
                        .padding(bottom = 36.dp)
                        .size(40.dp)
                )
                // Click-catcher: also stops lite mode's default "open Google Maps".
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { goToMapsPage(lat, lon) }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (lat != null && lon != null) goToMapsPage(lat, lon)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data?.addressName ?: "",
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!data?.addressDescription.isNullOrBlank()) {
                    Text(
                        text = data?.addressDescription ?: "",
                        fontFamily = robotoFontFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}

// ── Seller Section ──────────────────────────────────────────────

@Composable
private fun SellerSection(
    data: PostDetailsData?,
    context: android.content.Context,
    onMessageClicked: () -> Unit = {},
    onSellerClicked: (Int) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(top = 12.dp)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(20.dp), clip = false)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        SectionHeader(title = stringResource(id = R.string.contact_details))
        Spacer(modifier = Modifier.height(12.dp))

        // Seller card — tappable, opens the seller's public profile.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { data?.user?.id?.let(onSellerClicked) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile image
            val profileUrl = data?.user?.profileUrl
            if (!profileUrl.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = "${BuildConfig.BASE_URL}user/image/$profileUrl"
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (data?.user?.name?.firstOrNull() ?: "?").toString(),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = data?.user?.name ?: "",
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // Premium seller badge next to the name (matches the card crown).
                    if (data?.user?.premium == true) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(uz.promo.selling.ui.theme.PremiumGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_premium_crown_flat),
                                contentDescription = stringResource(R.string.premium_title),
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
                Text(
                    text = data?.user?.phoneNumber ?: "",
                    fontFamily = robotoFontFamily,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Message seller (hidden on your own listing)
        if (data != null && data.user.id != SharedPref.userId) {
            ContactActionButton(
                icon = Icons.AutoMirrored.Filled.Chat,
                label = stringResource(R.string.message_seller),
                modifier = Modifier.fillMaxWidth(),
                prominent = data.user.premium,
                onClick = onMessageClicked
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ContactActionButton(
                icon = Icons.Filled.Phone,
                label = stringResource(R.string.call),
                modifier = Modifier.weight(1f),
                prominent = data?.user?.premium == true,
                onClick = { callTo(data?.user?.phoneNumber ?: "", context) }
            )
            ContactActionButton(
                icon = Icons.Filled.Sms,
                label = stringResource(R.string.sms),
                modifier = Modifier.weight(1f),
                onClick = { openSmsApp(context, data?.user?.phoneNumber ?: "") }
            )
        }
    }
}

@Composable
private fun ContactActionButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    // Premium sellers get the promised "prominent" gold-filled contact buttons.
    prominent: Boolean = false,
    onClick: () -> Unit
) {
    val background = if (prominent) uz.promo.selling.ui.theme.PremiumGold
    else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)
    val foreground = if (prominent) Color.White else MaterialTheme.colorScheme.primary
    Row(
        modifier = modifier
            .background(background, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = foreground
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = foreground
        )
    }
}

// ── Bottom Action Bar ───────────────────────────────────────────

@Composable
fun OptionsScreen(
    modifier: Modifier,
    hazeState: HazeState,
    onDeleteClicked: (Int) -> Unit,
    onEditClicked: (Int) -> Unit,
    onCallClicked: () -> Unit,
    onSmsClicked: () -> Unit,
    data: PostDetailsData?
) {
    if (data?.user?.id == SharedPref.userId) {
        val shape = RoundedCornerShape(28.dp)
        val surfaceColor = MaterialTheme.colorScheme.surface
        Row(
            modifier = modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = shape,
                    ambientColor = Color.Black.copy(alpha = 0.25f),
                    spotColor = Color.Black.copy(alpha = 0.25f)
                )
                .clip(shape)
                .hazeEffect(state = hazeState) {
                    backgroundColor = surfaceColor
                    blurRadius = 24.dp
                    noiseFactor = 0f
                    tints = listOf(HazeTint(surfaceColor.copy(alpha = 0.50f)))
                    fallbackTint = HazeTint(surfaceColor.copy(alpha = 0.92f))
                }
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.30f), Color.White.copy(alpha = 0.06f))
                    ),
                    shape = shape
                )
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularImage(
                icon = Icons.Filled.Delete,
                tint = MaterialTheme.colorScheme.error,
                onClicked = { onDeleteClicked.invoke(data.id) }
            )
            CircularImage(
                icon = Icons.Filled.Edit,
                onClicked = { onEditClicked.invoke(data.id) }
            )
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
}


// ── Buyer Sticky Action Bar ─────────────────────────────────────

@Composable
private fun BuyerActionBar(
    modifier: Modifier,
    hazeState: HazeState,
    data: PostDetailsData,
    onCallClicked: () -> Unit,
    onMessageClicked: () -> Unit
) {
    val shape = RoundedCornerShape(28.dp)
    val surfaceColor = MaterialTheme.colorScheme.surface
    Column(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = shape,
                    ambientColor = Color.Black.copy(alpha = 0.25f),
                    spotColor = Color.Black.copy(alpha = 0.25f)
                )
                .clip(shape)
                // Liquid glass: the content scrolling underneath blurs through.
                .hazeEffect(state = hazeState) {
                    backgroundColor = surfaceColor
                    blurRadius = 24.dp
                    noiseFactor = 0f
                    tints = listOf(HazeTint(surfaceColor.copy(alpha = 0.50f)))
                    fallbackTint = HazeTint(surfaceColor.copy(alpha = 0.92f))
                }
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.30f), Color.White.copy(alpha = 0.06f))
                    ),
                    shape = shape
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Price
            Column(modifier = Modifier.weight(1f)) {
                PriceWrapper(data.category.post_param) { label, unit ->
                    Text(
                        text = formatPrice(label, unit),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))

            // Message (secondary)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f))
                    .clickable { onMessageClicked() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = stringResource(R.string.message_seller),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))

            // Call (primary CTA) — gradient pill.
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.82f)
                            )
                        )
                    )
                    .clickable { onCallClicked() }
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Phone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.call),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}


@Composable
fun CircularImage(
    icon: ImageVector = Icons.Filled.Delete,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    onClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.12f))
            .clickable { onClicked() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(21.dp)
        )
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
internal fun NavigationBarSpacer() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsBottomHeight(WindowInsets.navigationBars)
            .background(MaterialTheme.colorScheme.surface)
    )
}

// ── Reusable DescriptionItems (used by AddProductWizardComponents) ──

@Composable
fun DescriptionItems(
    imageVector: ImageVector = Icons.Filled.LocationOn,
    title: String = stringResource(id = R.string.address),
    desc: String,
    onClicked: () -> Unit
) {
    Row(
        modifier = Modifier.clickable { onClicked() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = imageVector, contentDescription = null,
            tint = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.SpaceEvenly) {
            Text(
                text = title,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = desc,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    }
}

// ── Utilities ───────────────────────────────────────────────────

/**
 * Renders a denormalized price as the plain digit string [formatPrice] expects
 * (it parses with toLongOrNull, so "500000.0" would fall through unformatted).
 */
private fun Double.toPlainPriceLabel(): String =
    if (this % 1.0 == 0.0) toLong().toString() else toString()

private fun formatPrice(label: String, unit: String): String {
    val number = label.toLongOrNull() ?: return "$label $unit"
    val formatted = String.format("%,d", number).replace(',', ' ')
    return if (unit.isNotBlank()) "$formatted $unit" else formatted
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTimeAgo(context: android.content.Context, dateString: String?): String {
    if (dateString.isNullOrBlank()) return ""
    return try {
        val date = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val now = LocalDateTime.now()
        val minutes = ChronoUnit.MINUTES.between(date, now)
        val hours = ChronoUnit.HOURS.between(date, now)
        val days = ChronoUnit.DAYS.between(date, now)
        val months = ChronoUnit.MONTHS.between(date, now)

        when {
            minutes < 1 -> context.getString(R.string.time_just_now)
            minutes < 60 -> context.getString(R.string.time_minutes_ago, minutes)
            hours < 24 -> context.getString(R.string.time_hours_ago, hours)
            days < 30 -> context.getString(R.string.time_days_ago, days)
            months < 12 -> context.getString(R.string.time_months_ago, months)
            else -> date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        }
    } catch (e: Exception) {
        dateString.take(10)
    }
}
