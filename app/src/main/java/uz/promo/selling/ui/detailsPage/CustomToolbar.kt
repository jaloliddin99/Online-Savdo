package uz.promo.selling.ui.detailsPage

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.showProducts.PostDetailsData
import uz.promo.selling.ui.theme.spacing


@Composable
fun DetailsToolbar(
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
    onBackClick: () -> Unit,
    onLikeClicked: (Int) -> Unit,
    onShareClick: () -> Unit = {},
    data: PostDetailsData?
) {
    val paddingValues = WindowInsets.systemBars.asPaddingValues()
    // Bouncy pop when the like state flips.
    val likeScale by animateFloatAsState(
        targetValue = if (data?.isLiked == true) 1.18f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "toolbarLike"
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = paddingValues.calculateTopPadding())
            .height(56.dp)
            .padding(horizontal = MaterialTheme.spacing.dimen6Dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScrimIconButton(hazeState = hazeState, onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }
        Spacer(modifier = modifier.weight(1f))
        ScrimIconButton(hazeState = hazeState, onClick = { data?.id?.let { onLikeClicked(it) } }) {
            if (data?.isLiked == true) {
                Image(
                    painter = painterResource(id = R.drawable.heart_filled),
                    contentDescription = null,
                    modifier = Modifier.scale(likeScale)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.heart_unfilled_2),
                    contentDescription = null,
                    modifier = Modifier.scale(likeScale)
                )
            }
        }
        Spacer(modifier = modifier.width(MaterialTheme.spacing.dimen6Dp))
        ScrimIconButton(hazeState = hazeState, onClick = onShareClick) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

/**
 * Icon button on a liquid-glass circle — the photo behind it blurs through.
 * Falls back to a plain translucent scrim when no [hazeState] is provided.
 */
@Composable
private fun ScrimIconButton(
    hazeState: HazeState?,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val glass = if (hazeState != null) {
        Modifier
            .clip(CircleShape)
            .hazeEffect(state = hazeState) {
                blurRadius = 20.dp
                noiseFactor = 0f
                tints = listOf(HazeTint(Color.Black.copy(alpha = 0.18f)))
                fallbackTint = HazeTint(Color.Black.copy(alpha = 0.35f))
            }
            .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
    } else {
        Modifier.background(Color.Black.copy(alpha = 0.35f), CircleShape)
    }
    Box(modifier = Modifier.padding(2.dp).then(glass)) {
        IconButton(onClick = onClick) { content() }
    }
}

@Composable
fun TopShadow() {
    // Soft contrast scrim so the glass buttons stay legible over bright photos.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.38f), Color.Black.copy(alpha = 0f))
                )
            )
    )
}
