package org.don.onlineTrade.ui.region

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.don.onlineTrade.data.remote.models.region.Data
import org.don.onlineTrade.ui.theme.spacing


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionItem(
    item: Data,
    onCategoryItemClick: (Data) -> Unit,
) {

    Column {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            onClick = {
                onCategoryItemClick(item)
            },
            shape = RoundedCornerShape(0.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = MaterialTheme.spacing.dimen12Dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {

                Text(
                    text = item.name,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
        Divider(modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 12.dp)
            .height(0.3.dp))
    }

}