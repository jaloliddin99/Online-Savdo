package org.don.onlineTrade.ui.categoriesList

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import org.don.onlineTrade.data.remote.models.category.CompactedCategoryItem
import org.don.onlineTrade.ui.home.CategoryItemInVertical
import org.don.onlineTrade.ui.home.HomeScreenState
import org.don.onlineTrade.ui.home.HomeViewModel
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading

@Composable
fun CategoriesRoute(
    modifier: Modifier = Modifier,
    onBackPressed: (CompactedCategoryItem) -> Unit
) {
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val state = homeViewModel.state.value
    CategoriesScreen(
        modifier = modifier, state = state, onBackPressed = onBackPressed
    )
}


@Composable
fun CategoriesScreen(
    modifier: Modifier,
    state: HomeScreenState,
    onBackPressed: (CompactedCategoryItem) -> Unit
) {

    val isFeedLoading = state.isLoading
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.dimen16Dp)
    ) {
        LazyColumn {
            if (state.registerMain != null) {
                itemsIndexed(state.registerMain) { index, item ->
                    CategoryItemInVertical(
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

        FreeLoading(isFeedLoading = isFeedLoading)

    }


}