package org.don.onlineTrade.ui.detailsPage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import org.don.onlineTrade.R
import org.don.onlineTrade.ui.home.PresentProductState
import org.don.onlineTrade.ui.theme.spacing

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePager(
    state: PresentProductState,
    pagerState: PagerState
) {
    var isLoading by remember {
        mutableStateOf(true)
    }
    var isError by remember {
        mutableStateOf(false)
    }

    val prefix = "http://91.227.40.169:8080/api/v1/post/image/"

    val suffix = state.registerMain?.data?.images?.get(pagerState.currentPage)?.imagePath
    val url  = "$prefix$suffix"

    val imageLoader = rememberAsyncImagePainter(model = url,
        onState = { statePager ->
            isLoading = statePager is AsyncImagePainter.State.Loading
            isError = statePager is AsyncImagePainter.State.Error
        })
    val customShape = RoundedCornerShape(16.dp)

    HorizontalPager(state = pagerState,
        modifier = Modifier.fillMaxWidth()
            .height(300.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.dimen16Dp)
                .background(shape = customShape, color = MaterialTheme.colorScheme.surface)
            ,
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
                contentScale = ContentScale.Inside,
                painter = if (isError.not()) imageLoader else painterResource(R.drawable.logo),
            )
        }
    }
}