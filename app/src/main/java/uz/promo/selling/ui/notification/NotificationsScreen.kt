package uz.promo.selling.ui.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uz.promo.selling.R
import uz.promo.selling.ui.TopAppBar
import uz.promo.selling.ui.filterCategory.ComposeLottieAnimation
import uz.promo.selling.ui.theme.spacing
import uz.promo.selling.utils.FreeLoading

@Composable
fun NotificationsRoute(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = stringResource(R.string.notifications),
            navigationIcon = Icons.Filled.ArrowBack,
            onNavigationClick = onBackClick,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )
        NotificationsScreen(
            modifier = modifier.weight(1f),
        )
    }
}


@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
) {

    val homeViewModel = hiltViewModel<NotificationViewModel>()
    val pagerState = homeViewModel.pagerState

    val scrollState = rememberLazyGridState()
    LaunchedEffect(key1 = homeViewModel){
        homeViewModel.loadNextItems()
    }
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn{
            items(pagerState.items.size) { i ->
                val item = pagerState.items[i]
                LaunchedEffect(scrollState) {
                    if (i >= pagerState.items.size - 1 && !pagerState.endReached && !pagerState.isLoading) {
                        homeViewModel.loadNextItems()
                    }
                }
                NotificationItemScreen(
                    item
                )
            }
            item {

                if (pagerState.isLoading && pagerState.page != 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            item {
                Spacer(modifier = modifier.height(MaterialTheme.spacing.dimen16Dp))
            }
        }
        if (!pagerState.isLoading && pagerState.endReached && pagerState.items.isEmpty()){
            ComposeLottieAnimation(Modifier)
        }
        FreeLoading(pagerState.isLoading)
    }

}