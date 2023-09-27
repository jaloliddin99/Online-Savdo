package org.don.onlineTrade.ui.add

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
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
import coil.compose.rememberAsyncImagePainter
import org.don.onlineTrade.ui.theme.spacing


@Composable
fun ShowSelectedImages(
    onAddButtonClicked: () -> Unit,
    imagesList: List<ImageUrl>
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
                    ImagesDisplayView(imageUrl)
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
            .height(48.dp)
        ,

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
fun ImagesDisplayView(imageUrl: ImageUrl) {
    Box(
        modifier = Modifier
            .width(80.dp)
            .fillMaxHeight()
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = imageUrl.uri), contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.dimen8Dp)
                .align(Alignment.Center),
            contentScale = ContentScale.Crop,
        )
        Icon(
            imageVector = Icons.Filled.Cancel, contentDescription = null,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}


@Composable
fun ProductTitle(@StringRes title: Int) {
    Text(
        text = stringResource(id = title),
        fontWeight = FontWeight.Medium,
        style = MaterialTheme.typography.titleSmall
    )
}


data class ImageUrl(
    val isFromCamera: Boolean,
    val uri: Uri,
    val fakeUri: Uri
)

fun Uri.toImageUrl(isFromCamera: Boolean): ImageUrl{
    return ImageUrl(
        isFromCamera = isFromCamera,
        this,
        this
    )
}
