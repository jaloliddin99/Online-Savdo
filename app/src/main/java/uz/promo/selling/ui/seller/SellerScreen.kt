package uz.promo.selling.ui.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import uz.promo.selling.BuildConfig
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.getProfile.SellerInfo
import uz.promo.selling.ui.TopAppBar
import uz.promo.selling.ui.main.home.ProductItem
import uz.promo.selling.ui.theme.PremiumGold
import uz.promo.selling.ui.theme.spacing
import uz.promo.selling.utils.FreeLoading

@Composable
fun SellerRoute(
    onItemClicked: (Int) -> Unit,
    onBackClick: () -> Unit,
) {
    val viewModel = hiltViewModel<SellerViewModel>()
    val seller = viewModel.seller
    val products = viewModel.products.collectAsLazyPagingItems()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = seller?.name ?: stringResource(R.string.seller_profile_title),
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onBackClick,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )
        SellerHeader(seller)
        Box(modifier = Modifier.weight(1f)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(
                    count = products.itemCount,
                    // peek() avoids triggering a Paging load during key resolution, which
                    // would otherwise eagerly load every page. See HomeScreen.
                    key = { index -> products.peek(index)?.id ?: index }
                ) { i ->
                    products[i]?.let { item ->
                        ProductItem(item, onItemClicked = onItemClicked, onItemLongLicked = {})
                    }
                }
                if (products.loadState.append is LoadState.Loading) {
                    item(span = { GridItemSpan(2) }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen16Dp))
                }
            }
            if (products.loadState.refresh is LoadState.NotLoading && products.itemCount == 0) {
                EmptySellerPosts()
            }
            FreeLoading(products.loadState.refresh is LoadState.Loading)
        }
    }
}

@Composable
private fun SellerHeader(seller: SellerInfo?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val profileUrl = seller?.profileUrl
        if (!profileUrl.isNullOrBlank()) {
            AsyncImage(
                model = "${BuildConfig.BASE_URL}user/image/$profileUrl",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (seller?.name?.firstOrNull() ?: '?').toString().uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = seller?.name ?: "",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Premium seller badge next to the name (matches the details page crown).
                if (seller?.premium == true) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(PremiumGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_premium_crown_flat),
                            contentDescription = stringResource(R.string.premium_title),
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
            Text(
                text = stringResource(R.string.seller_listings),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun EmptySellerPosts() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.not_order))
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            modifier = Modifier
                .width(150.dp)
                .height(150.dp),
            composition = composition,
            iterations = LottieConstants.IterateForever,
        )
        Text(
            text = stringResource(id = R.string.seller_no_posts),
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.titleSmall
        )
    }
}
