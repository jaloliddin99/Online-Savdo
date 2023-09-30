package org.don.onlineTrade.ui.region

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import org.don.onlineTrade.data.remote.models.region.RegionDistrictModelItem
import org.don.onlineTrade.ui.home.RegionsScreenState
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading

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
            .background(color = Color.Yellow)
            .padding(MaterialTheme.spacing.dimen16Dp)
    ) {
        FreeLoading(isFeedLoading = isFeedLoading)
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



    }


}