package uz.promo.selling.ui.main.home.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import uz.promo.selling.BuildConfig
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.getPublicProducts.Content
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.utils.SharedPref
import uz.promo.selling.utils.formatNumberWithSpaces

/** A search result placed on the map. */
private data class PostClusterItem(val post: Content) : ClusterItem {
    private val pos = LatLng(post.latitude ?: 0.0, post.longitude ?: 0.0)
    override fun getPosition(): LatLng = pos
    override fun getTitle(): String = post.title
    override fun getSnippet(): String = post.price
    override fun getZIndex(): Float = 0f
}

/**
 * Full-screen map of search results. Markers cluster so they never overlap;
 * tapping a single marker raises a bubble card that opens the post on click.
 */
@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun SearchMapView(
    posts: List<Content>,
    isLoading: Boolean,
    onPostClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = remember(posts) {
        posts.filter { it.latitude != null && it.longitude != null }
            .map { PostClusterItem(it) }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(SharedPref.latitude.toDouble(), SharedPref.longitude.toDouble()), 12f
        )
    }

    // Fit the camera to all markers whenever the result set changes.
    LaunchedEffect(items) {
        if (items.isEmpty()) return@LaunchedEffect
        if (items.size == 1) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(items.first().position, 15f), 600
            )
        } else {
            val builder = LatLngBounds.builder()
            items.forEach { builder.include(it.position) }
            runCatching {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(builder.build(), 140), 600
                )
            }
        }
    }

    var selectedPost by remember { mutableStateOf<Content?>(null) }

    // Follow the app theme: load the dark map style in dark mode (same raw style
    // the post-location map uses).
    val context = LocalContext.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val mapStyle = remember(isDark) {
        if (isDark) MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark) else null
    }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapStyleOptions = mapStyle),
            onMapClick = { selectedPost = null },
        ) {
            Clustering(
                items = items,
                onClusterItemClick = { item ->
                    selectedPost = item.post
                    true // we show our own bubble card, suppress the default info window
                },
                clusterItemContent = { item -> PriceMarker(item.post) },
                clusterContent = { cluster -> ClusterBadge(cluster.size) },
            )
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        selectedPost?.let { post ->
            PostBubbleCard(
                post = post,
                onClick = { onPostClick(post.id) },
                onClose = { selectedPost = null },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .navigationBarsPadding(),
            )
        }
    }
}

@Composable
private fun PriceMarker(post: Content) {
    Box(
        modifier = Modifier
            .shadow(3.dp, RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = "${formatNumberWithSpaces(post.price)} ${post.priceUnit}".trim(),
            color = MaterialTheme.colorScheme.onPrimary,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            maxLines = 1,
        )
    }
}

@Composable
private fun ClusterBadge(count: Int) {
    Box(
        modifier = Modifier
            .shadow(3.dp, CircleShape)
            .size(42.dp)
            .background(MaterialTheme.colorScheme.primary, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (count > 99) "99+" else "$count",
            color = MaterialTheme.colorScheme.onPrimary,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun PostBubbleCard(
    post: Content,
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val url = post.image?.imagePath?.let { "${BuildConfig.BASE_URL}post/image/$it?size=thumb" }
        Image(
            painter = rememberAsyncImagePainter(model = url, error = androidx.compose.ui.res.painterResource(R.drawable.sotiq_icon)),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = post.title,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${formatNumberWithSpaces(post.price)} ${post.priceUnit}".trim(),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
            )
            if (post.addressName.isNotBlank()) {
                Text(
                    text = post.addressName,
                    fontFamily = robotoFontFamily,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
        }
    }
}
