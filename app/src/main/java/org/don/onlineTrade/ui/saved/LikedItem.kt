package org.don.onlineTrade.ui.saved

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.liked.LikedProductsModelItem
import org.don.onlineTrade.ui.theme.spacing


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedItem(data: LikedProductsModelItem,
                onItemClicked: (Int) -> Unit,
                paddingValues: PaddingValues = PaddingValues(
                    start = MaterialTheme.spacing.dimen16Dp,
                    top = MaterialTheme.spacing.dimen16Dp,
                )
) {
    Card(
        modifier = Modifier
            .padding(paddingValues)
            .aspectRatio(0.7f),

        shape = RoundedCornerShape(MaterialTheme.spacing.dimen12Dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = {
            onItemClicked(data.id)
        },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
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

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.spacing.dimen6Dp),
                )
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
                Text(
                    text = data.date,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.spacing.dimen6Dp),
                )
            }

        }
    }
}