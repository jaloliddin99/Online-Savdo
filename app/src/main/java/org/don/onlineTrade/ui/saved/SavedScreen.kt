package org.don.onlineTrade.ui.saved

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import org.don.onlineTrade.ui.home.LikedProductsState
import org.don.onlineTrade.ui.home.ProductItem
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading


@Composable
fun SavedRoute(
    modifier: Modifier = Modifier,
    state: LikedProductsState
) {
    SavedScreen(
        modifier = modifier,
        state = state
    )
}

@Composable
fun SavedScreen(
    modifier: Modifier,
    state: LikedProductsState
) {

    val isFreeLoading = state.isLoading
    if (isFreeLoading){
        FreeLoading(isFreeLoading)
    }
    if (state.registerMain != null){
        LazyVerticalGrid(columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(end = MaterialTheme.spacing.dimen16Dp)
                .fillMaxSize(),
        ){
            items(state.registerMain){
                LikedItem(
                    data = it,
                    onItemClicked = {
                    }
                )
            }
        }
    }
}

