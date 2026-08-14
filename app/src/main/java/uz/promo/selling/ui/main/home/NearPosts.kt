package uz.promo.selling.ui.main.home

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
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
import uz.promo.selling.BuildConfig
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.nearPost.NearPostsData
import uz.promo.selling.ui.theme.LocalCustomColors
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.ui.theme.spacing
import uz.promo.selling.utils.shimmerBrush

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
        // Near posts is a quick "around you" strip, not a full feed — cap it at 20,
        // matching the server-side limit, however wide the radius is.
        val items = state.take(20)
        items.forEachIndexed { index, item ->
            Spacer(modifier = modifier.width(MaterialTheme.spacing.dimen8Dp))
            NearPostItem(
                item = item,
                navigateToCategory = navigateToCategory
            )
            if (index == items.lastIndex) {
                Spacer(modifier = modifier.width(MaterialTheme.spacing.dimen8Dp))
            }
        }
    }
}


@Composable
fun NearPostsEmpty(
    modifier: Modifier = Modifier,
    /** Opens the location/radius map. Null hides the action. */
    onUpdateLocation: (() -> Unit)? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(MaterialTheme.spacing.dimen12Dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = MaterialTheme.spacing.dimen8Dp,
                end = MaterialTheme.spacing.dimen8Dp
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.dimen12Dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.no_posts_for_location),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (onUpdateLocation != null) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))
                // Deliberately compact: this sits on the main feed, so it must read
                // as a hint rather than a full-width call to action.
                FilledTonalButton(
                    onClick = onUpdateLocation,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .wrapContentWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.height(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(id = R.string.update_location),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
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
            val url = item.image?.imagePath?.let {
                "${BuildConfig.BASE_URL}post/image/$it?size=thumb"
            }
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
                    text = distanceLabel(item.distance),
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

/**
 * Distance label for the "near you" strip. The API reports metres, which only read
 * sensibly at walking range — since the strip honours the user's search radius (up
 * to 500 km) anything past a kilometre is shown in km instead of "263065 m away".
 */
@Composable
private fun distanceLabel(meters: Int): String =
    if (meters < 1000) {
        "$meters ${stringResource(id = R.string.m)}"
    } else {
        val km = (meters / 100) / 10.0
        "$km ${stringResource(id = R.string.km_away)}"
    }
