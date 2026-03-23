package uz.don.selling.ui.main.home.homeItems

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import uz.don.selling.ui.theme.spacing
import uz.don.selling.utils.shimmerEffect

@Composable
fun ShimmerProductItem(modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .padding(
                start = MaterialTheme.spacing.dimen8Dp,
                top = MaterialTheme.spacing.dimen16Dp,
                end = MaterialTheme.spacing.dimen8Dp
            )
    ) {
        Column(modifier = Modifier.wrapContentHeight()) {
            // Image area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .shimmerEffect()
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.dimen8Dp)
            ) {
                // Title
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(6.dp))
                // Likes
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Location
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(6.dp))
                // Price row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(50))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

@Composable
fun ShimmerCategoryItem(modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(MaterialTheme.spacing.dimen12Dp),
        modifier = modifier
            .width(MaterialTheme.spacing.dimen100Dp)
            .height(MaterialTheme.spacing.dimen120Dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.spacing.dimen120Dp)
                .padding(
                    start = MaterialTheme.spacing.dimen8Dp,
                    end = MaterialTheme.spacing.dimen8Dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Circle image
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
            // Text
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }
    }
}

@Composable
fun ShimmerNearPostItem(modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(MaterialTheme.spacing.dimen12Dp),
        modifier = modifier
            .height(60.dp)
            .width(180.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            // Image
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(MaterialTheme.spacing.dimen8Dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
            }
        }
    }
}

@Composable
fun ShimmerProductGrid(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        repeat(3) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ShimmerProductItem(modifier = Modifier.weight(1f))
                ShimmerProductItem(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun ShimmerCategoriesRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        repeat(4) {
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen8Dp))
            ShimmerCategoryItem()
        }
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen8Dp))
    }
}

@Composable
fun ShimmerNearPostsRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        repeat(3) {
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen8Dp))
            ShimmerNearPostItem()
        }
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen8Dp))
    }
}
