package uz.promo.selling.ui.main.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import uz.promo.selling.BuildConfig
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.getPublicProducts.Content
import uz.promo.selling.ui.theme.LocalCustomColors
import uz.promo.selling.ui.theme.PremiumGold
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.ui.theme.spacing
import uz.promo.selling.utils.convertDate
import uz.promo.selling.utils.formatNumberWithSpaces
import uz.promo.selling.utils.shimmerBrush


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductItem(
    data: Content,
    onItemClicked: (Int) -> Unit,
    paddingValues: PaddingValues = PaddingValues(
        start = MaterialTheme.spacing.dimen8Dp,
        top = MaterialTheme.spacing.dimen16Dp,
        end = MaterialTheme.spacing.dimen8Dp
    ),
    isLiked: Boolean = false,
    isMyPosts: Boolean = false,
    onItemLongLicked: (Int) -> Unit,
    imageModifier: Modifier = Modifier,
    onLikeClicked: ((Int) -> Unit)? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(paddingValues)
            .wrapContentWidth()
            .combinedClickable(
                onClick = {
                    onItemClicked(data.id)
                },
                onLongClick = {
                    onItemLongLicked.invoke(data.id)
                },
            )
    ) {
        ProductItemDetails(data, isLiked, isMyPosts, imageModifier, onLikeClicked)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItemForDetailsPage(
    data: Content,
    onItemClicked: (Int) -> Unit,
    paddingValues: PaddingValues = PaddingValues(
        start = MaterialTheme.spacing.dimen16Dp,
        top = MaterialTheme.spacing.dimen16Dp,
    )
) {
    val itemSize: Dp = (LocalConfiguration.current.screenWidthDp.dp / 2) - 24.dp

    Card(
        shape = RoundedCornerShape(MaterialTheme.spacing.dimen12Dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .padding(paddingValues)
            .width(itemSize),
        onClick = {
            onItemClicked(data.id)
        }
    ) {
        ProductItemDetails(data)
    }
}


@Composable
fun ProductItemDetails(
    data: Content,
    isLiked: Boolean = false,
    isMyPosts: Boolean = false,
    imageModifier: Modifier = Modifier,
    onLikeClicked: ((Int) -> Unit)? = null,
) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.wrapContentHeight()
    ) {
        var isLoading by remember {
            mutableStateOf(true)
        }

        // Null-safe: a post may have no image (some imports). Coil shows the
        // shimmer/placeholder background instead of crashing.
        val url = data.image?.imagePath?.let {
            "${BuildConfig.BASE_URL}post/image/$it?size=thumb"
        }

        // Build the request once per url instead of on every recomposition — this is
        // re-created for every visible card when the Home tab is re-entered.
        val context = LocalContext.current
        val imageRequest = remember(url) {
            ImageRequest.Builder(context)
                .data(url)
                .crossfade(true)
                .build()
        }

        Box(
            modifier = imageModifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            AsyncImage(
                model = imageRequest,
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(R.string.cd_photo),
                modifier = Modifier
                    .fillMaxSize()
                    .background(LocalCustomColors.current.imageBackgroundColor)
                    .background(shimmerBrush(targetValue = 1300f, showShimmer = isLoading)),
                onSuccess = { isLoading = false },
                onError = { isLoading = false },
            )

            if (onLikeClicked != null) {
                // Like button — toggle without opening the post details.
                val likeScale by animateFloatAsState(
                    targetValue = if (isLiked) 1.18f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "likeScale"
                )
                Box(
                    // Bottom-end: the top-end corner belongs to the premium crown badge.
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.30f))
                        .clickable { onLikeClicked(data.id) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isLiked) R.drawable.heart_filled else R.drawable.heart_unfilled
                        ),
                        contentDescription = stringResource(R.string.cd_like),
                        tint = if (isLiked) Color(0xFFFF5252) else Color.White,
                        modifier = Modifier
                            .size(17.dp)
                            .scale(likeScale)
                    )
                }
            } else if (isLiked) {
                Icon(
                    // Bottom-end like the interactive heart — the top-end corner
                    // belongs to the premium crown badge.
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(24.dp),
                    painter = painterResource(id = R.drawable.heart_filled),
                    contentDescription = stringResource(R.string.cd_like),
                    tint = Color.White
                )
            }

            data.condition?.let {
                Text(
                    text = it,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    color = Color.White,
                    fontFamily = robotoFontFamily,
                    fontSize = 9.sp,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
            }

            if (data.isPrioritized){
                Text(
                    text = stringResource(id = R.string.top),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(topStart = 12.dp, bottomEnd = 12.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(start = 6.dp, end = 6.dp, top = 1.dp, bottom = 2.dp),
                    color = Color.White,
                    fontFamily = robotoFontFamily,
                    fontSize = 12.sp
                )
            }

            // Premium seller badge — a small gold crown chip. Hidden on the
            // owner's own cards, where the ⋮ actions menu occupies that corner.
            if (data.sellerIsPremium && !isMyPosts) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(PremiumGold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_premium_crown_flat),
                        contentDescription = stringResource(R.string.cd_premium),
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            if (isMyPosts && data.status != 1) {
                Text(
                    text = stringResource(
                        id = when (data.status) {
                            0 -> R.string.pending
                            2 -> R.string.rejected
                            3 -> R.string.sold
                            else -> R.string.expired
                        }
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(start = 4.dp, end = 4.dp, top = 1.dp, bottom = 2.dp),
                    color = Color.White,
                    fontFamily = robotoFontFamily,
                    fontSize = 9.sp
                )
            }

        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(MaterialTheme.spacing.dimen8Dp),
        ) {
            Text(
                text = data.title,
                modifier = Modifier
                    .fillMaxWidth(),
                fontSize = 14.sp,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val currentColor = LocalContentColor.current
            val colorWithAlpha = currentColor.copy(alpha = 0.7f)

            val likesText = if (data.likes == 0 || data.likes == 1)
                "${data.likes} ${stringResource(id = R.string.likeSingular)}"
            else
                "${data.likes} ${stringResource(id = R.string.likePulural)}"
            Text(
                text = if (isMyPosts)
                    "$likesText • ${data.viewCount} ${stringResource(id = R.string.views_count)}"
                else likesText,
                fontSize = 12.sp,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                color = colorWithAlpha,
            )
            Text(
                text = data.addressName ?: "",
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen4Dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    text = "${formatNumberWithSpaces(data.price)} ${data.priceUnit ?: ""}",
                    fontSize = 12.sp,
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
                Text(
                    text = convertDate(data.createdDate),
                    fontFamily = robotoFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}
