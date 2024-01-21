package org.don.onlineTrade.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.getPublicProducts.Content
import org.don.onlineTrade.data.remote.models.getPublicProducts.Region
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.convertDate


@OptIn(ExperimentalMaterial3Api::class)
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
    isMainScreenOrProfile: Boolean = true
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .padding(paddingValues)
            //.aspectRatio(0.6f),
            .wrapContentWidth(),
        onClick = {
            onItemClicked(data.id)
        }
    ) {
        ProductItemDetails(data, isLiked, isMainScreenOrProfile)
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
    isMainScreenOrProfile: Boolean = true
) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.wrapContentHeight()
    ) {
        var isLoading by remember {
            mutableStateOf(true)
        }
        var isError by remember {
            mutableStateOf(false)
        }
        val url = "http://91.227.40.169:8080/api/v1/post/image/${data.image.imagePath}"
        val imageLoader = rememberAsyncImagePainter(model = url,
            onState = { state ->
                isLoading = state is AsyncImagePainter.State.Loading
                isError = state is AsyncImagePainter.State.Error
            })

        Box(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(80.dp),
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
            Image(
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Inside,
                painter = if (isError.not()) imageLoader else painterResource(R.drawable.logo),
            )
        }
        val paddingValues = PaddingValues(
            bottom = MaterialTheme.spacing.dimen8Dp,
            start = MaterialTheme.spacing.dimen8Dp,
            end = MaterialTheme.spacing.dimen8Dp,
            top = MaterialTheme.spacing.dimen8Dp,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(paddingValues),
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                val (text, icon) = createRefs()
                Text(
                    text = data.title,
                    modifier = Modifier
                        .constrainAs(text) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(icon.start)
                            width = Dimension.fillToConstraints
                        }
                        .fillMaxWidth(),
                    fontSize = 14.sp,
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                val shape = RoundedCornerShape(8.dp)

                if (!isMainScreenOrProfile) {
                    Icon(
                        painter = painterResource(id = if (isLiked) R.drawable.ph_heart_fill else R.drawable.solar_heart_outline),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .width(MaterialTheme.spacing.dimen32Dp)
                            .height(MaterialTheme.spacing.dimen32Dp)
                            .padding(MaterialTheme.spacing.dimen4Dp)
                            .clip(shape)
                            .constrainAs(icon) {
                                end.linkTo(parent.end)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                            .clickable {

                            }
                    )
                }

            }

            Text(
                text = stringResource(id = R.string.txt_new),
                fontSize = 14.sp,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen4Dp))

            Text(
                text = "${data.price} ${data.priceUnit}",
                fontSize = 14.sp,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen4Dp))
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
            Text(
                text = convertDate(data.createdDate),
                fontFamily = robotoFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen4Dp))
        }
    }
}
