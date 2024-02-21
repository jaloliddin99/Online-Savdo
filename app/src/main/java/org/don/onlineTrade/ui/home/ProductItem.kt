package org.don.onlineTrade.ui.home

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.don.onlineTrade.BuildConfig
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.getPublicProducts.Content
import org.don.onlineTrade.ui.theme.LocalCustomColors
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.convertDate
import org.don.onlineTrade.utils.formatNumberWithSpaces
import org.don.onlineTrade.utils.shimmerBrush


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    onItemLongLicked: (Int) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
        ProductItemDetails(data, isLiked, isMyPosts)
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
) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.wrapContentHeight()
    ) {
        var isLoading by remember {
            mutableStateOf(true)
        }

        val url = "${BuildConfig.BASE_URL}post/image/${data.image.imagePath}"

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val (imageRef, likeRef, textRef, pendingRef, prioritized) = createRefs()

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = "Loaded Image",
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(imageRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .background(LocalCustomColors.current.imageBackgroundColor)
                    .background(shimmerBrush(targetValue = 1300f, showShimmer = isLoading)),
                onSuccess = { isLoading = false },
                onError = { isLoading = false },
            )

            if (isLiked) {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .constrainAs(likeRef) {
                            top.linkTo(parent.top, margin = 8.dp)
                            end.linkTo(parent.end, margin = 8.dp)
                        },
                    painter = painterResource(id = R.drawable.heart_filled),
                    contentDescription = "Like",
                    tint = Color.White
                )
            }



            data.condition?.let {
                Text(
                    text = it,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary)
                        .constrainAs(textRef) {
                            bottom.linkTo(parent.bottom, margin = 4.dp)
                            start.linkTo(parent.start, margin = 4.dp)
                        }
                        .padding(start = 4.dp, end = 4.dp, top = 1.dp, bottom = 2.dp),
                    color = Color.White,
                    fontFamily = robotoFontFamily,
                    fontSize = 9.sp
                )
            }

            if (data.isPrioritized){
                Text(
                    text = stringResource(id = R.string.top),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary)
                        .constrainAs(prioritized) {
                            bottom.linkTo(parent.top, margin = 4.dp)
                            end.linkTo(parent.start, margin = 4.dp)
                        }
                        .padding(start = 4.dp, end = 4.dp, top = 1.dp, bottom = 2.dp),
                    color = Color.White,
                    fontFamily = robotoFontFamily,
                    fontSize = 12.sp
                )
            }

            if (isMyPosts && (data.status == 0 || data.status == 2)) {
                Text(
                    text = stringResource(
                        id = if (data.status == 0) R.string.pending
                        else R.string.rejected
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary)
                        .constrainAs(pendingRef) {
                            bottom.linkTo(parent.bottom, margin = 4.dp)
                            end.linkTo(parent.end, margin = 4.dp)
                        }
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
            val colorWithAlpha = currentColor.copy(alpha = 0.7f) // Adjust alpha as needed

            Text(
                text = "${data.likes} ${stringResource(id = R.string.liked)}",
                fontSize = 12.sp,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                color = colorWithAlpha,
            )
            val region = data.region?.name
            val district = data.district?.name
            val name = data.addressName

            Text(
                text = if (region != null && district != null) "$region, $district"
                else name,
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
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(start = 4.dp, end = 4.dp, top = 1.dp, bottom = 2.dp),
                    text = "${formatNumberWithSpaces(data.price)} ${data.priceUnit}",
                    fontSize = 12.sp,
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
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
