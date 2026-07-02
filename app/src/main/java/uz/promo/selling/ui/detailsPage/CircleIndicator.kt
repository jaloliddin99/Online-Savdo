package uz.promo.selling.ui.detailsPage

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import uz.promo.selling.BuildConfig
import uz.promo.selling.R
import uz.promo.selling.ui.main.home.PresentProductState

@Composable
fun ImagePager(
    state: PresentProductState,
    pagerState: PagerState,
    sharedImageModifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    val prefix = "${BuildConfig.BASE_URL}post/image/"

    // getOrNull (not get): a post may have 0 images, so index 0 is out of bounds.
    val suffix = state.registerMain?.images?.getOrNull(pagerState.currentPage)?.imagePath
    val url = suffix?.let { "$prefix$it?size=mid" }

    // Reuse the grid card's cached thumbnail as the placeholder — the shared-element
    // transition from Home lands on the image instantly instead of a spinner while
    // the mid-size version loads.
    val context = LocalContext.current
    val imageRequest = remember(url) {
        ImageRequest.Builder(context)
            .data(url)
            .apply {
                suffix?.let { placeholderMemoryCacheKey("$prefix$it?size=thumb") }
            }
            .crossfade(true)
            .build()
    }

    val imageLoader = rememberAsyncImagePainter(model = imageRequest,
        onState = { statePager ->
            isLoading = statePager is AsyncImagePainter.State.Loading
            isError = statePager is AsyncImagePainter.State.Error
        })

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(410.dp)
        ) { page ->

            // The first page is the same image as the Home card — it morphs from/to it.
            Box(modifier = if (page == 0) sharedImageModifier else Modifier) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(80.dp),
                    )
                }
                Image(
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    painter = if (isError.not()) imageLoader else painterResource(R.drawable.sotiq_icon),
                )
            }
        }

        CircularPagerIndicator(
            itemCount = pagerState.pageCount,
            currentPage = pagerState.currentPage
        )

        // Photo counter pill (e.g. "2/5") — only with multiple images.
        if (pagerState.pageCount > 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.45f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${pagerState.currentPage + 1}/${pagerState.pageCount}",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun CircularPagerIndicator(
    modifier: Modifier = Modifier,
    itemCount: Int,
    currentPage: Int,
    indicatorSize: Dp = 7.dp,
    indicatorColor: Color = Color.White.copy(alpha = 0.5f)
) {
    // The "n/m" counter pill covers many-image posts; dots would overflow.
    if (itemCount <= 1 || itemCount > 8) return
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color.Black.copy(alpha = 0.28f))
                .padding(horizontal = 8.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(itemCount) { index ->
                val selected = index == currentPage
                // Active dot stretches into a pill and slides as you swipe.
                val width by animateDpAsState(
                    targetValue = if (selected) 18.dp else indicatorSize,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "pagerDot"
                )
                Box(
                    modifier = Modifier
                        .height(indicatorSize)
                        .width(width)
                        .clip(RoundedCornerShape(50))
                        .background(if (selected) Color.White else indicatorColor)
                )
            }
        }
    }
}