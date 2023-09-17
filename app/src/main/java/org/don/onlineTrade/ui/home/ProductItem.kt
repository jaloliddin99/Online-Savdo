package org.don.onlineTrade.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
import org.don.onlineTrade.ui.theme.spacing


@Composable
fun ProductsItemsList(pagingItems: LazyPagingItems<Data>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .height(1000.dp)
            .padding(horizontal = MaterialTheme.spacing.dimen8Dp),
        content = {
            val items = pagingItems.itemSnapshotList.items
            items(items) {
                ProductItem(it)
            }
        }
    )
}

@Composable
fun ProductItem(data: Data) {
    Card(
        modifier = Modifier
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
            AsyncImage(
                model = data.images[0],
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f),
                contentScale = ContentScale.Crop,
            )
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