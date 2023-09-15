package org.don.onlineTrade.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import org.don.onlineTrade.data.remote.models.category.CategoryModel
import org.don.onlineTrade.data.remote.models.category.CategoryModelItem
import org.don.onlineTrade.data.remote.models.category.CompactedCategoryItem


@Composable
fun HomeRoute(
    modifier: Modifier = Modifier
) {
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val state = homeViewModel.state.value
    HomeScreen(modifier = modifier, state = state)
}

@Composable
fun HomeScreen(
    modifier: Modifier,
    state: HomeScreenState
) {
    val context = LocalContext.current

    Box {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        if (state.error.isNotBlank()) {
            Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {

        item {
            if (state.registerMain != null) {
                Categories(state.registerMain)
            }
        }

    }
}

@Composable
fun Categories(homeScreenState: List<CompactedCategoryItem>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(homeScreenState) {
            CategoryItem(
                item = it, modifier = Modifier
                    .width(100.dp)
                    .height(120.dp)
            )
        }

        mainPagingProducts()
    }
}

fun LazyListScope.mainPagingProducts() {
    items(20) {
        Text(
            text = "THis is demo text",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun CategoryItem(
    item: CompactedCategoryItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        AsyncImage(
            modifier = Modifier
                .width(80.dp)
                .height(80.dp),
            model = item.image, contentDescription = null
        )

        Text(text = item.title)
    }
}


@Preview
@Composable
fun CategoryItemPreview() {
    CategoryItem(
        item = CompactedCategoryItem(
            12,
            "https://thumbs.dreamstime.com/b/environment-earth-day-hands-trees-growing-seedlings-bokeh-green-background-female-hand-holding-tree-nature-field-gra-130247647.jpg",
            "hello"
        ),
        modifier = Modifier
            .width(100.dp)
            .height(120.dp)
    )
}

