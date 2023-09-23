package org.don.onlineTrade.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.don.onlineTrade.data.remote.models.category.CompactedCategoryItem
import org.don.onlineTrade.ui.theme.spacing

@Composable
fun Categories(state: List<CompactedCategoryItem>) {
    LazyRow(verticalAlignment = Alignment.CenterVertically) {
        itemsIndexed(state) { index, item ->
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen12Dp))
            CategoryItem(
                item = item, modifier = Modifier
                    .width(MaterialTheme.spacing.dimen100Dp)
                    .height(MaterialTheme.spacing.dimen120Dp)
            )
            if (index == state.lastIndex) {
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen12Dp))
            }
        }
    }
}


@Composable
fun CategoryItem(
    item: CompactedCategoryItem,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.clickable {  },
        shape = RoundedCornerShape(MaterialTheme.spacing.dimen12Dp),
        ) {
        Column(
            modifier = modifier
                .padding(
                    start = MaterialTheme.spacing.dimen12Dp,
                    end = MaterialTheme.spacing.dimen12Dp
                )
                .clip(RoundedCornerShape(MaterialTheme.spacing.dimen12Dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            AsyncImage(
                modifier = Modifier
                    .width(MaterialTheme.spacing.dimen80Dp)
                    .height(MaterialTheme.spacing.dimen80Dp),
                model = item.image, contentDescription = null
            )

            Text(
                text = item.title,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
    }
}