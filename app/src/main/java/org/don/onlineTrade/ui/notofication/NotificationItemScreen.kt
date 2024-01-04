package org.don.onlineTrade.ui.notofication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.getPublicProducts.Content
import org.don.onlineTrade.data.remote.models.getPublicProducts.Region
import org.don.onlineTrade.ui.home.getCurrency
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.convertDate


@Composable
fun NotificationItemScreen(
    data: Content,
    paddingValues: PaddingValues = PaddingValues(
        start = MaterialTheme.spacing.dimen16Dp,
        top = MaterialTheme.spacing.dimen12Dp,
        end = MaterialTheme.spacing.dimen16Dp
    )
) {

    Card(
        shape = RoundedCornerShape(MaterialTheme.spacing.dimen12Dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .padding(paddingValues)
            .wrapContentWidth()
    ) {
        NotificationItemView(data)
    }

}

@Preview
@Composable
fun NotificationItemViewPreView() {
    NotificationItemView(data = Content(
        "2023-12-10T21:09:51.895279",
        1,
        1,
        org.don.onlineTrade.data.remote.models.getPublicProducts.Image(1, "awd"),
        23,
        2.3,
        Region(1, "Jizzax"),
        "This is a description text "
    ))
}


@Composable
fun NotificationItemView(
    data: Content
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
                contentScale = ContentScale.None,
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
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(paddingValues),
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen4Dp))
            Text(
                text = "${data.price} ${getCurrency(currencyId = data.currency_id)}",
                fontSize = 14.sp,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
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