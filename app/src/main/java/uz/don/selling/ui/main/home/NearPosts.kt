package uz.don.selling.ui.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import uz.don.selling.BuildConfig
import uz.don.selling.R
import uz.don.selling.data.remote.models.nearPost.NearPostsData
import uz.don.selling.ui.theme.LocalCustomColors
import uz.don.selling.ui.theme.robotoFontFamily
import uz.don.selling.ui.theme.spacing
import uz.don.selling.utils.shimmerBrush

@Composable
fun NearPosts(
    state: List<NearPostsData>,
    navigateToCategory: (Int) -> Unit,
    modifier: Modifier = Modifier

) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(
                rememberScrollState()
            )
    ) {
        state.forEachIndexed { index, item ->
            Spacer(modifier = modifier.width(MaterialTheme.spacing.dimen8Dp))
            NearPostItem(
                item = item,
                navigateToCategory = navigateToCategory
            )
            if (index == state.lastIndex) {
                Spacer(modifier = modifier.width(MaterialTheme.spacing.dimen8Dp))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearPostItem(
    item: NearPostsData,
    modifier: Modifier = Modifier,
    navigateToCategory: (Int) -> Unit,
    shape: Shape = RoundedCornerShape(MaterialTheme.spacing.dimen8Dp)

) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(MaterialTheme.spacing.dimen12Dp),
        onClick = {
            navigateToCategory(item.id)
        },
        modifier = modifier
            .height(60.dp)
            .width(180.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            val url = "${BuildConfig.BASE_URL}post/image/${item.image.imagePath}"
            val showShimmer = remember { mutableStateOf(true) }

            AsyncImage(
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp)
                    .padding(8.dp)
                    .clip(shape)
                    .background(
                        LocalCustomColors.current.imageBackgroundColor,
                        shape = RoundedCornerShape(MaterialTheme.spacing.dimen4Dp)
                    )
                    .background(shimmerBrush(targetValue = 1300f, showShimmer = showShimmer.value)),
                model = url,
                contentScale = ContentScale.Crop,
                onSuccess = { showShimmer.value = false },
                contentDescription = null,
            )

            Spacer(modifier = modifier.width(8.dp))
            Column(
                verticalArrangement = Arrangement.SpaceAround,
            ) {
                Text(
                    text = item.title,
                    textAlign = TextAlign.Center,
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    maxLines = 1
                )
                Text(
                    text = "${item.distance} ${stringResource(id = R.string.m)}",
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    maxLines = 1,
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
