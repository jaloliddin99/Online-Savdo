package org.don.onlineTrade.ui.detailsPage

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import org.don.onlineTrade.BuildConfig
import org.don.onlineTrade.R
import org.don.onlineTrade.ui.home.PresentProductState
import org.don.onlineTrade.ui.theme.spacing

@Composable
fun ImagePager(
    state: PresentProductState,
    pagerState: PagerState
) {
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    val prefix = "${BuildConfig.BASE_URL}post/image/"

    val suffix = state.registerMain?.data?.images?.get(pagerState.currentPage)?.imagePath
    val url = "$prefix$suffix"

    val imageLoader = rememberAsyncImagePainter(model = url,
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
                .height(350.dp)
        ) {

            Box {
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
                    painter = if (isError.not()) imageLoader else painterResource(R.drawable.logo),
                )
            }
        }

        CircularPagerIndicator(
            itemCount = pagerState.pageCount,
            currentPage = pagerState.currentPage
        )
    }
}

@Composable
fun CircularPagerIndicator(
    modifier: Modifier = Modifier,
    itemCount: Int,
    currentPage: Int,
    indicatorSize: Dp = 8.dp,
    indicatorColor: Color = Color.Gray

) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(
            modifier = Modifier
                .wrapContentSize()
                .background(color = Color.Transparent),
            horizontalArrangement = Arrangement.Center
        ) {
            items(itemCount) { index ->
                CircularIndicator(
                    selected = index == currentPage,
                    size = indicatorSize,
                    color = indicatorColor,
                    selectedSize = indicatorSize * 1.1f // Adjust the factor for the selected circle size
                )
            }
        }
    }
}

@Composable
fun CircularIndicator(
    selected: Boolean,
    size: Dp,
    color: Color,
    selectedSize: Dp
) {
    val finalSize = if (selected) selectedSize else size
    val selectedColor = if (selected) Color.White else color

    Canvas(
        modifier = Modifier
            .padding(1.2.dp)
            .size(finalSize)
    ) {
        drawRoundRect(
            color = selectedColor,
            size = Size(finalSize.toPx(), finalSize.toPx()),
            cornerRadius = CornerRadius(50f)
        )
    }
}