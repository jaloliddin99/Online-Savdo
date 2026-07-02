package uz.promo.selling.ui.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image
import uz.promo.selling.BuildConfig
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.payments.PostStat
import uz.promo.selling.ui.TopAppBar
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.utils.formatNumberWithSpaces

@Composable
fun AnalyticsRoute(
    onBack: () -> Unit,
    onGetPremium: () -> Unit,
    onPostClick: (Long) -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel(),
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = stringResource(R.string.analytics_title),
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onBack,
        )

        when {
            viewModel.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            viewModel.premiumRequired -> PremiumLocked(onGetPremium)
            else -> {
                val data = viewModel.data
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Outlined.Visibility,
                                value = formatNumberWithSpaces((data?.totalViews ?: 0).toString()),
                                label = stringResource(R.string.analytics_total_views)
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Filled.Favorite,
                                value = formatNumberWithSpaces((data?.totalLikes ?: 0).toString()),
                                label = stringResource(R.string.analytics_total_likes)
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Outlined.Inventory2,
                                value = (data?.activeCount ?: 0).toString(),
                                label = stringResource(R.string.analytics_active_ads)
                            )
                        }
                    }
                    if (data == null || data.posts.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.analytics_no_posts),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.padding(top = 24.dp)
                            )
                        }
                    } else {
                        items(data.posts) { post ->
                            PostStatRow(post = post, onClick = { onPostClick(post.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, icon: ImageVector, value: String, label: String) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(6.dp))
        Text(value, fontFamily = robotoFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(
            label, fontFamily = robotoFontFamily, fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
            maxLines = 2
        )
    }
}

@Composable
private fun PostStatRow(post: PostStat, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val url = post.image?.let { "${BuildConfig.BASE_URL}post/image/$it?size=thumb" }
        Image(
            painter = rememberAsyncImagePainter(url),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = post.title ?: "",
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        StatPill(Icons.Outlined.Visibility, post.viewCount)
        Spacer(Modifier.width(8.dp))
        StatPill(Icons.Filled.Favorite, post.likes)
    }
}

@Composable
private fun StatPill(icon: ImageVector, value: Long) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon, null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.size(15.dp)
        )
        Spacer(Modifier.width(3.dp))
        Text(
            text = "$value",
            fontFamily = robotoFontFamily,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

/** Shown when a standard user opens a premium-only feature. */
@Composable
fun PremiumLocked(onGetPremium: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = androidx.compose.ui.res.painterResource(R.drawable.ic_premium_crown),
            contentDescription = null,
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.premium_locked_msg),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(20.dp))
        Button(onClick = onGetPremium, shape = RoundedCornerShape(14.dp)) {
            Text(stringResource(R.string.get_premium), fontWeight = FontWeight.SemiBold)
        }
    }
}
