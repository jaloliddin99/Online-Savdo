package uz.don.selling.ui.main.home.homeItems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
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
import uz.don.selling.R
import uz.don.selling.ui.theme.spacing
import uz.don.selling.utils.SharedPref

@Composable
fun HomeSearchBar(
    onSearchClick: () -> Unit,
    onMapClick: () -> Unit,
    onNotificationClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    searchBarModifier: Modifier = Modifier,
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
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
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
                shape = RoundedCornerShape(32.dp),
                singleLine = true,
                modifier = searchBarModifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(end = 48.dp)
                    .padding(vertical = 16.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .clickable(onClick = onSearchClick)
            )
        }
        IconButton(onClick = onNotificationClick) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}