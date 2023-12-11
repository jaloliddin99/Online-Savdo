package org.don.onlineTrade.ui.region

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import org.don.onlineTrade.data.remote.models.region.Data
import org.don.onlineTrade.ui.home.RegionsScreenState
import org.don.onlineTrade.utils.FreeLoading

@Composable
fun RegionsRoute(modifier: Modifier = Modifier,
                 onBackPressed: (Data) -> Unit
) {
    val regionsViewModel = hiltViewModel<RegionsViewModel>()
    val state = regionsViewModel.state.value
    RegionsScreen(
        modifier = modifier, state = state, onBackPressed = onBackPressed
    )
}


@Composable
fun RegionsScreen(
    modifier: Modifier = Modifier,
    state: RegionsScreenState,
    onBackPressed: (Data) -> Unit
) {

    val isFeedLoading = state.isLoading
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
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
                }
            }
        }
        FreeLoading(isFeedLoading = isFeedLoading)

        if (state.error.isNotBlank()) {
            Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
        }



    }


}