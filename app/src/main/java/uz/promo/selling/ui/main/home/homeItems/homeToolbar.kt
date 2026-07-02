package uz.promo.selling.ui.main.home.homeItems

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import uz.promo.selling.R
import uz.promo.selling.ui.theme.spacing
import uz.promo.selling.utils.SharedPref

/**
 * Home search field — a liquid-glass pill when [hazeState] is provided (the feed
 * scrolling underneath blurs through it), a regular filled field otherwise.
 */
@Composable
fun HomeSearchBar(
    onSearchClick: () -> Unit,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier,
    searchBarModifier: Modifier = Modifier,
    hazeState: HazeState? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.dimen8Dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            val searchHint = if (SharedPref.locationName.isNotBlank()) {
                stringResource(R.string.search_hint_location, SharedPref.locationName, SharedPref.radius)
            } else {
                stringResource(R.string.search_hint_default)
            }

            val fieldShape = RoundedCornerShape(32.dp)
            val surfaceColor = MaterialTheme.colorScheme.surface
            val glass = if (hazeState != null) {
                Modifier
                    .clip(fieldShape)
                    .hazeEffect(state = hazeState) {
                        backgroundColor = surfaceColor
                        blurRadius = 20.dp
                        noiseFactor = 0f
                        tints = listOf(HazeTint(surfaceColor.copy(alpha = 0.55f)))
                        fallbackTint = HazeTint(surfaceColor.copy(alpha = 0.92f))
                    }
                    .border(1.dp, Color.White.copy(alpha = 0.25f), fieldShape)
            } else {
                Modifier
            }

            TextField(
                value = "",
                onValueChange = {},
                enabled = false,
                placeholder = {
                    Text(
                        text = searchHint,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                },
                colors = if (hazeState != null) {
                    // Transparent container — the glass behind provides the surface.
                    TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    )
                } else {
                    TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onMapClick) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                shape = fieldShape,
                singleLine = true,
                modifier = searchBarModifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
                    .then(glass)
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(end = 48.dp)
                    .padding(vertical = 4.dp)
                    .clip(fieldShape)
                    .clickable(onClick = onSearchClick)
            )
        }
    }
}
