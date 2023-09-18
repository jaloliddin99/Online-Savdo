package org.don.onlineTrade.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.SizeMode
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
import org.don.onlineTrade.ui.theme.spacing


@Composable
fun ProductsItemsList(pagingItems: LazyPagingItems<Data>) {
    val itemSize: Dp = (LocalConfiguration.current.screenWidthDp.dp / 2)

    com.google.accompanist.flowlayout.FlowRow(
        mainAxisSize = SizeMode.Expand,
        mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween,
    ) {
        val items = pagingItems.itemSnapshotList.items
        items.forEachIndexed { index, data ->
            ProductItem(data, itemSize)
        }
    }
}

@Composable
fun ProductItem(data: Data, itemSize: Dp) {
    Card(

        modifier = Modifier
            .width(itemSize)
            .padding(
                start = MaterialTheme.spacing.dimen8Dp,
                top = MaterialTheme.spacing.dimen16Dp,
                end = MaterialTheme.spacing.dimen8Dp
            )
            .aspectRatio(0.7f),
        shape = RoundedCornerShape(MaterialTheme.spacing.dimen12Dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            verticalArrangement = Arrangement.Top
        ) {
            var isLoading by remember {
                mutableStateOf(true)
            }
            var isError by remember {
                mutableStateOf(false)
            }
            val imageLoader = rememberAsyncImagePainter(model = data.images[0],
                onState = { state ->
                    isLoading = state is AsyncImagePainter.State.Loading
                    isError = state is AsyncImagePainter.State.Error
                })

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f),
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
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    painter = if (isError.not()) imageLoader else painterResource(R.drawable.ic_launcher_background),
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen6Dp))
            Text(
                text = data.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.spacing.dimen6Dp),
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen4Dp))
            Text(
                text = data.price,
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.spacing.dimen6Dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(MaterialTheme.spacing.dimen4Dp)
                    ),
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen4Dp))
            Text(
                text = data.region.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.spacing.dimen6Dp),
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen4Dp))

            Text(
                text = data.date,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.spacing.dimen6Dp),
            )
        }
    }
}