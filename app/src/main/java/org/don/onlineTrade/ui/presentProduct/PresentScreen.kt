package org.don.onlineTrade.ui.presentProduct

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import org.don.onlineTrade.ui.home.PresentProductState
import org.don.onlineTrade.ui.home.TOKEN
import org.don.onlineTrade.utils.FreeLoading
import org.don.onlineTrade.utils.SharedPref


@Composable
fun PresentRoute(
    productId: Int
) {
    val homeViewModel = hiltViewModel<PresentViewModel>()

    LaunchedEffect(key1 = "hello"){
        homeViewModel.getProductDetail(
            id = productId,
            language = SharedPref.language,
            token = TOKEN
        )
    }

    val state = homeViewModel.state.value
    PresentScreen(
        state = state
    )
}

@Composable
fun PresentScreen(
    modifier: Modifier = Modifier,
    state: PresentProductState,

) {

    val isFeedLoading = state.isLoading

    val context = LocalContext.current

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        FreeLoading(isFeedLoading = isFeedLoading)
    }

}