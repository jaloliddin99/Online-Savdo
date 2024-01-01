package org.don.onlineTrade.ui.categoriesList

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    LaunchedEffect(key1 = homeViewModel) {
        homeViewModel.getAllCategories()
    }

    CategoriesScreen(
        modifier = modifier,
        state = state,
        onBackPressed = onBackPressed
    )
}

@Preview
@Composable
fun CategoriesScreenPreView() {
    CategoriesScreen(state = HomeScreenState(), onBackPressed = {})
}

@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    state: HomeScreenState,
    onBackPressed: (CompactedCategoryItem) -> Unit
) {
    val isFeedLoading = state.isLoading
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .height(1000.dp)
        ) {
            if (state.registerMain?.get(-1) != null) {
                itemsIndexed(state.registerMain[-1]!!) { index, item ->
                    var newList by remember { mutableStateOf(emptyList<CompactedCategoryItem>()) }

                    var isExpanded by remember { mutableStateOf(false) }

                    CategoryItemInVertical(
                        modifier = Modifier,
                        item = item,
                        onCategoryItemClick = {
                            newList = state.registerMain[it.id]!!
                            isExpanded = !isExpanded
                        },
                        isExpanded
                    )

                    if (isExpanded){
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .height((newList.size * 56.3).dp)
                        ) {
                            itemsIndexed(newList) { _, item ->
                                CategoryItemInVertical(
                                    modifier = Modifier.padding(start = 32.dp),
                                    item = item,
                                    onCategoryItemClick = {
                                        onBackPressed.invoke(it)
                                    },
                                    isExpanded = true,
                                    displayArrow = false
                                )
                            }
                        }
                    }

                }
            }

        }

        if (state.error.isNotBlank()) {
            Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
        }

        FreeLoading(isFeedLoading = isFeedLoading)

    }


}