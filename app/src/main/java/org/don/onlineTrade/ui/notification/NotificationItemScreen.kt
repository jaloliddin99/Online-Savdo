package org.don.onlineTrade.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.don.onlineTrade.BuildConfig
import org.don.onlineTrade.data.remote.models.getNotifications.Content
import org.don.onlineTrade.ui.theme.LocalCustomColors
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.convertDate
import org.don.onlineTrade.utils.shimmerBrush


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


@Composable
fun NotificationItemView(
    data: Content
) {
    Column(
        modifier = Modifier.wrapContentHeight()
    ) {
        var isLoading by remember {
            mutableStateOf(true)
        }
        val currentColor = LocalContentColor.current

        val url = "${BuildConfig.BASE_URL}notification/image/${data.imagePath}"

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = "Loaded Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(LocalCustomColors.current.imageBackgroundColor)
                .background(shimmerBrush(targetValue = 1300f, showShimmer = isLoading)),
            onSuccess = {
                isLoading = false
            },
            onError = {
                isLoading = false
            },
        )
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
            val colorWithAlpha = currentColor.copy(alpha = 0.7f) // Adjust alpha as needed
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen4Dp))
            Text(
                text = data.title,
                fontSize = 16.sp,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen4Dp))
            Text(
                text = data.desc,
                fontSize = 14.sp,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = convertDate(data.createdDate),
                fontFamily = robotoFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = colorWithAlpha,
                textAlign = TextAlign.End
            )
        }
    }
}