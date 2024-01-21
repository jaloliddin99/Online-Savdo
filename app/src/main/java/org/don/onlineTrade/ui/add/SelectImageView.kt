package org.don.onlineTrade.ui.add

import android.net.Uri
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing


@Composable
fun ShowSelectedImages(
    onAddButtonClicked: () -> Unit,
    imagesList: List<ImageUrl>,
    cancelClicked: (ImageUrl) -> Unit
) {

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(MaterialTheme.spacing.dimen12Dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                imagesList.forEach { imageUrl ->
                    ImagesDisplayView(imageUrl, cancelClicked = cancelClicked)
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen8Dp))
                }
                ShowCircularImage {
                    onAddButtonClicked()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowCircularImage(
    onItemClicked: () -> Unit
) {

    OutlinedCard(
        modifier = Modifier
            .width(48.dp)
            .height(48.dp),

        onClick = onItemClicked,
        shape = RoundedCornerShape(24.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.Add, contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.dimen8Dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun ImagesDisplayView(
    imageUrl: ImageUrl,
    cancelClicked: (ImageUrl) -> Unit
) {
    Box(
        modifier = Modifier
            .width(80.dp)
            .fillMaxHeight()
    ) {
        AsyncImage(
            model = imageUrl.uri, contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.dimen8Dp)
                .align(Alignment.Center),
            contentScale = ContentScale.Crop,
        )

        Icon(
            imageVector = Icons.Filled.Cancel, contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable {
                    cancelClicked.invoke(imageUrl)
                }
        )
    }
}


@Composable
fun ProductTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Normal,
        fontFamily = robotoFontFamily,
        fontSize = MaterialTheme.spacing.dimen16Sp
    )
}

@Composable
fun TextThin(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Light,
        fontFamily = robotoFontFamily,
        fontSize = 14.sp
    )
}

@Composable
fun TextBold(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        fontWeight = FontWeight.SemiBold,
        fontFamily = robotoFontFamily,
        fontSize = MaterialTheme.spacing.dimen16Sp
    )
}


data class ImageUrl(
    val isFromCamera: Boolean,
    val uri: Uri,
    val fakeUri: Uri
)

fun Uri.toImageUrl(isFromCamera: Boolean, fakeUri: Uri): ImageUrl {
    return ImageUrl(
        isFromCamera = isFromCamera,
        this,
        fakeUri
    )
}
