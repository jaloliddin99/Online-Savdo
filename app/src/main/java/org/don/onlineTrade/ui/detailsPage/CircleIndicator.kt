package org.don.onlineTrade.ui.detailsPage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.don.onlineTrade.ui.home.PresentProductState
import org.don.onlineTrade.ui.theme.spacing

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePager(
    state: PresentProductState,
    pagerState: PagerState
) {
    HorizontalPager(state = pagerState) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(212.dp)
                .padding(MaterialTheme.spacing.dimen16Dp),

        ) {
            val prefix = "http://91.227.40.169:8080/api/v1/post/image/"

            val suffix = state.registerMain!!.data.images[0].imagePath
            val url  = "$prefix$suffix"
            AsyncImage(
                model = url, contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop

            )
        }
    }
}