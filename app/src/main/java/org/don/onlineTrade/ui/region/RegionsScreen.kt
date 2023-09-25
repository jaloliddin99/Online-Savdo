package org.don.onlineTrade.ui.region

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.category.CompactedCategoryItem
import org.don.onlineTrade.data.remote.models.region.RegionDistrictModelItem
import org.don.onlineTrade.ui.home.CategoryItemInVertical
import org.don.onlineTrade.ui.home.HomeScreenState
import org.don.onlineTrade.ui.home.HomeViewModel
import org.don.onlineTrade.ui.home.RegionsScreenState
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.OnlineMarketLoadingWheel

@Composable
fun RegionsRoute(modifier: Modifier = Modifier,
                 onBackPressed: (RegionDistrictModelItem) -> Unit
) {
    val regionsViewModel = hiltViewModel<RegionsViewModel>()
    val state = regionsViewModel.state.value
    RegionsScreen(
        modifier = modifier, state = state, onBackPressed = onBackPressed
    )
}


@Composable
fun RegionsScreen(
    modifier: Modifier,
    state: RegionsScreenState,
    onBackPressed: (RegionDistrictModelItem) -> Unit
) {

    val isFeedLoading = state.isLoading
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.dimen16Dp)
    ) {
        LazyColumn {
            if (state.regions != null) {
                itemsIndexed(state.regions) { index, item ->
                    RegionItem(
                        item = item,
                        onCategoryItemClick = {
                            onBackPressed.invoke(it)
                        }
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
                }
            }

        }

        if (state.error.isNotBlank()) {
            Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
        }

        AnimatedVisibility(
            visible = isFeedLoading,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> -fullHeight },
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight },
            ) + fadeOut(),
        ) {
            val loadingContentDescription = stringResource(id = R.string.for_you_loading)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                OnlineMarketLoadingWheel(
                    modifier = Modifier
                        .align(Alignment.Center),
                    contentDesc = loadingContentDescription,
                )
            }
        }

    }


}