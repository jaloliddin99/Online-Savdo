package uz.promo.selling.ui.main.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import uz.promo.selling.R
import uz.promo.selling.ui.TopAppBar
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.utils.FreeLoading

@Composable
fun NotificationSettingsRoute(
    onBackClick: () -> Unit,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = stringResource(R.string.notif_settings),
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onBackClick,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            PrefRow(
                title = stringResource(R.string.channel_chat),
                subtitle = stringResource(R.string.channel_chat_desc),
                checked = viewModel.chat,
                onChange = { viewModel.setPref("chat", it) }
            )
            PrefDivider()
            PrefRow(
                title = stringResource(R.string.channel_search),
                subtitle = stringResource(R.string.channel_search_desc),
                checked = viewModel.search,
                onChange = { viewModel.setPref("search", it) }
            )
            PrefDivider()
            PrefRow(
                title = stringResource(R.string.channel_my_ads),
                subtitle = stringResource(R.string.channel_my_ads_desc),
                checked = viewModel.myAds,
                onChange = { viewModel.setPref("myAds", it) }
            )
            PrefDivider()
            PrefRow(
                title = stringResource(R.string.channel_news),
                subtitle = stringResource(R.string.channel_news_desc),
                checked = viewModel.news,
                onChange = { viewModel.setNewsEnabled(it) }
            )
        }
    }
    FreeLoading(viewModel.isLoading)
}

@Composable
private fun PrefRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = robotoFontFamily,
                fontSize = 15.sp
            )
            Text(
                text = subtitle,
                fontFamily = robotoFontFamily,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun PrefDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
    )
}
