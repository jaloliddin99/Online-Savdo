package uz.promo.selling.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.inbox.UserNotificationItem
import uz.promo.selling.ui.filterCategory.ComposeLottieAnimation
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.utils.convertDate

/** Personal notification inbox ("For you" tab). */
@Composable
fun InboxScreen(
    modifier: Modifier = Modifier,
    onPostClick: (Int) -> Unit,
    viewModel: InboxViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadNext()
        viewModel.markAllRead()
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn {
            itemsIndexed(viewModel.items, key = { _, item -> item.id }) { index, item ->
                LaunchedEffect(index) {
                    if (index >= viewModel.items.size - 1) viewModel.loadNext()
                }
                InboxRow(item = item, onClick = {
                    item.refId?.let { onPostClick(it.toInt()) }
                })
                if (index < viewModel.items.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 68.dp, end = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    )
                }
            }
            item {
                if (viewModel.isLoading) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        if (viewModel.loaded && viewModel.items.isEmpty()) {
            ComposeLottieAnimation(Modifier)
        }
    }
}

@Composable
private fun InboxRow(item: UserNotificationItem, onClick: () -> Unit) {
    val (icon, label) = when (item.type) {
        "post_approved" -> Icons.Filled.CheckCircle to stringResource(R.string.notif_post_approved)
        "post_rejected" -> Icons.Filled.Cancel to stringResource(R.string.notif_post_rejected)
        "post_expired" -> Icons.Filled.Timer to stringResource(R.string.notif_post_expired)
        "boost_paid" -> Icons.Filled.RocketLaunch to stringResource(R.string.notif_boost_paid)
        "search_match" -> Icons.Filled.Search to stringResource(R.string.notif_search_match)
        else -> Icons.Filled.Notifications to (item.body ?: "")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = item.refId != null) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontFamily = robotoFontFamily,
                fontWeight = if (item.read) FontWeight.Normal else FontWeight.SemiBold,
                fontSize = 14.sp
            )
            if (!item.title.isNullOrBlank()) {
                Text(
                    text = item.title,
                    fontFamily = robotoFontFamily,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }
            item.createdDate?.let {
                Text(
                    text = convertDate(it),
                    fontFamily = robotoFontFamily,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
        if (!item.read) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}
