package org.don.onlineTrade.ui.region

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.don.onlineTrade.data.remote.models.region.Data
import org.don.onlineTrade.data.remote.models.region.DataDistrict
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing


@Composable
fun RegionItem(
    item: Data,
    onRegionClicked: (Data) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = MaterialTheme.spacing.dimen12Dp)
                .clickable {
                    onRegionClicked(item)
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = item.name,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                maxLines = 1,
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .height(0.3.dp)
        )

    }

}

@Composable
fun DistrictItem(
    item: DataDistrict,
    onDistrictClicked: (DataDistrict) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = MaterialTheme.spacing.dimen12Dp)
                .clickable {
                    onDistrictClicked(item)
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = item.name,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                maxLines = 1,
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .height(0.3.dp)
        )

    }

}