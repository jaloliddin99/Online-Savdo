package uz.promo.selling.ui.detailsPage

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
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
    navigateToChat: (Long) -> Unit = {}
) {
    val detailsViewModel = hiltViewModel<PresentViewModel>()
    val chatStartViewModel = hiltViewModel<ChatStartViewModel>()
    val routeContext = LocalContext.current
    val similarProducts = detailsViewModel.similarProducts.collectAsLazyPagingItems()

    LaunchedEffect(productId) {
        detailsViewModel.getProductDetail(
            id = productId,
            language = SharedPref.language,
        )
    }

    val state = detailsViewModel.state.value
    if (state.delete != null) {
        navigateBack.invoke()
    }

    ProductDetailsScreen(
        similarProducts = similarProducts,
        modifier = Modifier.fillMaxSize(),
        state = state,
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
        }
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
    onMessageClicked: () -> Unit = {}
) {

    val isFeedLoading = state.isLoading
    val data = state.registerMain

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
        Box(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
            ) {
                // Image pager
                item(key = "image_pager") {
                    ImagePager(state, pagerState)
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
                    SellerSection(data, context, onMessageClicked)
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

                // Bottom spacing
                item(key = "bottom_spacer") {
                    Spacer(modifier = Modifier.height(24.dp))
                    NavigationBarSpacer()
                }
            }

            // Bottom action bar
            OptionsScreen(
                modifier = Modifier.align(Alignment.BottomCenter),
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

            // Toolbar overlay
            TopShadow()
            DetailsToolbar(
                onBackClick = onBackPressed,
                onLikeClicked = onItemClicked,
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
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp)
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

        // Price
        PriceWrapper(data?.category?.post_param) { label, unit ->
            Text(
                text = formatPrice(label, unit),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Meta row: date, likes, category
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatTimeAgo(data?.createdDate),
                    fontFamily = robotoFontFamily,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Likes
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { data?.id?.let { onLikeClicked(it) } }
            ) {
                Icon(
                    imageVector = if (data?.isLiked == true) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (data?.isLiked == true) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${data?.likes ?: 0} ${stringResource(R.string.likePulural)}",
                    fontFamily = robotoFontFamily,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Views
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${data?.viewCount ?: 0}",
                    fontFamily = robotoFontFamily,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Category chip
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Category,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = data?.category?.title ?: "",
                    fontFamily = robotoFontFamily,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ── Description Section ─────────────────────────────────────────

@Composable
private fun DescriptionSection(description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        SectionHeader(title = stringResource(id = R.string.description))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
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
            .padding(top = 8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp)
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clickable {
                if (data?.latitude != null) {
                    goToMapsPage(data.latitude, data.longitude)
                }
            },
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
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
    onMessageClicked: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        SectionHeader(title = stringResource(id = R.string.contact_details))
        Spacer(modifier = Modifier.height(12.dp))

        // Seller card
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                Text(
                    text = data?.user?.name ?: "",
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
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
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// ── Bottom Action Bar ───────────────────────────────────────────

@Composable
fun OptionsScreen(
    modifier: Modifier,
    onDeleteClicked: (Int) -> Unit,
    onEditClicked: (Int) -> Unit,
    onCallClicked: () -> Unit,
    onSmsClicked: () -> Unit,
    data: PostDetailsData?
) {
    if (data?.user?.id == SharedPref.userId) {
        Column(modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .shadow(elevation = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularImage(
                    icon = Icons.Filled.Delete,
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
            NavigationBarSpacer()
        }
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
        elevation = FloatingActionButtonDefaults.elevation(0.dp),
        modifier = Modifier.size(48.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(22.dp))
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

private fun formatPrice(label: String, unit: String): String {
    val number = label.toLongOrNull() ?: return "$label $unit"
    val formatted = String.format("%,d", number).replace(',', ' ')
    return if (unit.isNotBlank()) "$formatted $unit" else formatted
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTimeAgo(dateString: String?): String {
    if (dateString.isNullOrBlank()) return ""
    return try {
        val date = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val now = LocalDateTime.now()
        val minutes = ChronoUnit.MINUTES.between(date, now)
        val hours = ChronoUnit.HOURS.between(date, now)
        val days = ChronoUnit.DAYS.between(date, now)
        val months = ChronoUnit.MONTHS.between(date, now)

        when {
            minutes < 1 -> "just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days < 30 -> "${days}d ago"
            months < 12 -> "${months}mo ago"
            else -> date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        }
    } catch (e: Exception) {
        dateString.take(10)
    }
}
