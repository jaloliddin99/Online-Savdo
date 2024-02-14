package org.don.onlineTrade.ui.detailsPage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.showProducts.Data
import org.don.onlineTrade.ui.theme.spacing


@Composable
fun DetailsToolbar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onLikeClicked: (Int) -> Unit,
    data: Data?
) {
    val paddingValues = WindowInsets.systemBars.asPaddingValues()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = paddingValues.calculateTopPadding())
            .height(56.dp)
            .padding(horizontal = MaterialTheme.spacing.dimen6Dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(modifier = modifier.weight(1f))
        IconButton(onClick = {
            data?.id?.let {
                onLikeClicked(it)
            }
        }) {
            if (data?.isLiked == true) {
                Image(
                    painter = painterResource(id = R.drawable.heart_filled),
                    contentDescription = null
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.heart_unfilled_2),
                    contentDescription = null
                )
            }
        }
        Spacer(modifier = modifier.width(MaterialTheme.spacing.dimen6Dp))
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

    }
}

@Composable
fun TopShadow() {
    val black = Color.Black.copy(alpha = 0.5f)
    val transparentBlack = Color.Black.copy(alpha = 0f)

    val gradientColors = listOf(black, transparentBlack)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                brush = Brush.verticalGradient(gradientColors),
            ),

        ) {

    }
}