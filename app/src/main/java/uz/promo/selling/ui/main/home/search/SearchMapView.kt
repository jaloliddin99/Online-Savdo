package uz.promo.selling.ui.main.home.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Rect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import uz.promo.selling.BuildConfig
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.getPublicProducts.Content
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.utils.SharedPref
import uz.promo.selling.utils.formatNumberWithSpaces

/**
 * A search result placed on the map. Identity is the post id only: `Content` is a
 * data class whose generated hashCode/equals NPE on its non-null String fields when
 * the backend sends null (e.g. a post without a price), and the clustering algorithm
 * stores items in a HashSet.
 */
private class PostClusterItem(val post: Content) : ClusterItem {
    private val pos = LatLng(post.latitude ?: 0.0, post.longitude ?: 0.0)
    override fun getPosition(): LatLng = pos
    override fun getTitle(): String? = post.title
    override fun getSnippet(): String? = post.price
    override fun getZIndex(): Float = 0f

    override fun equals(other: Any?): Boolean =
        this === other || (other is PostClusterItem && other.post.id == post.id)

    override fun hashCode(): Int = post.id
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
    // Pre-tile background color so the map opens in the right theme instead of
    // flashing white before tiles load. Matches the dark style's base (#212121).
    val mapBackground = if (isDark) 0xFF212121.toInt() else android.graphics.Color.WHITE

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapStyleOptions = mapStyle),
            // Hide the default Google Maps controls (zoom, my-location, compass,
            // and the marker toolbar that opens Google Maps).
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = false,
            ),
            googleMapOptionsFactory = { GoogleMapOptions().backgroundColor(mapBackground) },
            onMapClick = { selectedPost = null },
        ) {
            Clustering(
                items = items,
                onClusterItemClick = { item ->
                    selectedPost = item.post
                    true // we show our own bubble card, suppress the default info window
                },
                clusterItemContent = { item -> PostMarker(item.post) },
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

/** Teardrop pin shape: a round head (top, width × width) with a tail tipping at
 * the bottom-centre — the default Google marker anchor (0.5, 1.0) puts that tip
 * exactly on the coordinate. */
private val PinShape = GenericShape { size, _ ->
    addOval(Rect(0f, 0f, size.width, size.width))
    moveTo(size.width * 0.22f, size.width * 0.80f)
    lineTo(size.width / 2f, size.height)
    lineTo(size.width * 0.78f, size.width * 0.80f)
    close()
}

/** Uniform photo pin — same size/shape for every post, photo inside the head. */
@Composable
private fun PostMarker(post: Content) {
    val context = LocalContext.current
    val url = post.image?.imagePath?.let { "${BuildConfig.BASE_URL}post/image/$it?size=thumb" }
    // allowHardware(false): the cluster renderer draws markers on a software
    // canvas, which can't use Coil's default hardware bitmaps.
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .build(),
        error = painterResource(R.drawable.sotiq_icon),
    )
    Box(
        modifier = Modifier
            .size(width = 46.dp, height = 58.dp)
            .shadow(3.dp, PinShape)
            .clip(PinShape)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.TopCenter,
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(top = 3.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
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
            .shadow(14.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val url = post.image?.imagePath?.let { "${BuildConfig.BASE_URL}post/image/$it?size=thumb" }
        Image(
            painter = rememberAsyncImagePainter(model = url, error = androidx.compose.ui.res.painterResource(R.drawable.sotiq_icon)),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = post.title,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${formatNumberWithSpaces(post.price)} ${post.priceUnit ?: ""}".trim(),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
            )
            if (!post.addressName.isNullOrBlank()) {
                Text(
                    text = post.addressName,
                    fontFamily = robotoFontFamily,
                    fontSize = 13.sp,
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
