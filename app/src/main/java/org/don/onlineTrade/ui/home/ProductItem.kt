package org.don.onlineTrade.ui.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.currencies.ModelCurrencyListsItem
import org.don.onlineTrade.data.remote.models.getPublicProducts.Content
import org.don.onlineTrade.data.remote.models.getPublicProducts.Region
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItem(
    data: Content,
    onItemClicked: (Int) -> Unit,
    paddingValues: PaddingValues = PaddingValues(
        start = MaterialTheme.spacing.dimen8Dp,
        top = MaterialTheme.spacing.dimen16Dp,
        end = MaterialTheme.spacing.dimen8Dp
    )
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .padding(paddingValues)
            .aspectRatio(0.5f),
        onClick = {
            onItemClicked(data.id)
        }
    ) {
        ProductItemDetails(data)
    }
}

@Preview
@Composable
fun ProductItemPreView() {
    ProductItem(data = Content(
        "12-12-2023",
        1,
        1,
        org.don.onlineTrade.data.remote.models.getPublicProducts.Image(1, "awd"),
        23,
        2.3,
        Region(1, "Jizzax"),
        "dawdawd"
    ), onItemClicked = {})
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItemForDetailsPage(
    data: Content,
    onItemClicked: (Int) -> Unit,
    itemSize: Dp,
    paddingValues: PaddingValues = PaddingValues(
        start = MaterialTheme.spacing.dimen16Dp,
        top = MaterialTheme.spacing.dimen16Dp,
    )
) {
    Card(
        shape = RoundedCornerShape(MaterialTheme.spacing.dimen12Dp),
        modifier = Modifier
            .padding(paddingValues)
            .width(itemSize)
            .aspectRatio(0.7f),
        onClick = {
            onItemClicked(data.id)
        },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        ProductItemDetails(data)
    }
}


@Composable
fun ProductItemDetails(
    data: Content
) {
    Column(
        verticalArrangement = Arrangement.Top
    ) {
        var isLoading by remember {
            mutableStateOf(true)
        }
        var isError by remember {
            mutableStateOf(false)
        }
        val url = "http://91.227.40.169:8080/api/v1/post/image/${data.image.imagePath}"
        Log.d("TAG", "ProductItemDetaildawdwadwad $url")
        val imageLoader = rememberAsyncImagePainter(model = url,
            onState = { state ->
                isLoading = state is AsyncImagePainter.State.Loading
                isError = state is AsyncImagePainter.State.Error
            })

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f),
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
                    .fillMaxSize(),
                contentScale = ContentScale.Crop,
                painter = if (isError.not()) imageLoader else painterResource(R.drawable.ic_launcher_background),
            )
        }
        val paddingValues = PaddingValues(
            bottom = MaterialTheme.spacing.dimen8Dp,
            start = MaterialTheme.spacing.dimen8Dp,
            end = MaterialTheme.spacing.dimen8Dp
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            ConstraintLayout(
                modifier = Modifier.fillMaxWidth()
            ) {
                val (text, icon) = createRefs()

                Text(
                    text = data.title,
                    modifier = Modifier
                        .constrainAs(text) {
                            start.linkTo(parent.start)
                            end.linkTo(icon.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                        .wrapContentWidth(),
                    fontSize = 14.sp,
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                val shape = RoundedCornerShape(8.dp)

                Icon(
                    painter = painterResource(id = R.drawable.solar_heart_outline),
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

            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(MaterialTheme.spacing.dimen4Dp)
                    )
            ) {
                Text(
                    text = "${data.price} ${getCurrency(currencyId = data.currency_id)}",
                    fontSize = 14.sp,
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = data.region.name,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = data.createdDate,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun getCurrency(currencyId: Int): String {
    return when (currencyId) {
        1 -> stringResource(id = R.string.sum)
        2 -> stringResource(id = R.string.usd)
        3 -> stringResource(id = R.string.ruble)
        else -> stringResource(id = R.string.sum)
    }
}

@Composable
fun getCurrencyList(): List<ModelCurrencyListsItem> {
    return listOf(
        ModelCurrencyListsItem(1, stringResource(id = R.string.sum)),
        ModelCurrencyListsItem(2, stringResource(id = R.string.usd)),
        ModelCurrencyListsItem(3, stringResource(id = R.string.ruble))
    )
}