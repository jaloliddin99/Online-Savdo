package org.don.onlineTrade.ui.home.search

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.leak.Values
import org.don.onlineTrade.ui.filterCategory.ComposeLottieAnimation
import org.don.onlineTrade.ui.home.HomeViewModel
import org.don.onlineTrade.ui.home.ProductItem
import org.don.onlineTrade.ui.theme.spacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar


@Composable
fun SearchRoute(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onItemClick: (Int) -> Unit,
) {

    SearchScreen(
        modifier = modifier,
        onBackClick = onBackClick,
        onItemClick = onItemClick,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onItemClick: (Int) -> Unit = {},
) {

    var searchTextListener by remember {
        mutableStateOf("")
    }

    val homeViewModel = hiltViewModel<HomeViewModel>()
    val pagerState = homeViewModel.pagerState

    val scrollState = rememberLazyGridState()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }


    Box {
        Column(modifier = modifier) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
            SearchToolbar(
                onBackClick = onBackClick,
                onSearchQueryChanged = {
                    searchTextListener = it
                },
                onSearchTriggered = {
                    searchTextListener = it
                    homeViewModel.resetPager()
                    homeViewModel.loadNextItems(
                        query = searchTextListener
                    )
                },
                searchQuery = searchTextListener,
                onFilterClicked = {
                    showBottomSheet = true
                }
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = modifier
                    .padding(end = MaterialTheme.spacing.dimen16Dp)
                    .fillMaxSize(),
            ) {

                items(pagerState.items.size) { i ->
                    val item = pagerState.items[i]
                    LaunchedEffect(scrollState) {
                        if (i >= pagerState.items.size - 1 && !pagerState.endReached && !pagerState.isLoading) {
                            homeViewModel.loadNextItems(
                                query = searchTextListener
                            )
                        }
                    }
                    ProductItem(
                        item,
                        onItemClicked = onItemClick
                    )
                }
                item(span = { GridItemSpan(2) }) {
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

            }

        }
        if (!pagerState.isLoading && pagerState.endReached && pagerState.items.isEmpty()) {
            ComposeLottieAnimation(Modifier)
        }
    }



    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            modifier = modifier.fillMaxHeight()
        ) {
            BottomSheetContent(
                onClickListen = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                }
            )
        }
    }


}

@Composable
fun BottomSheetContent(
    onClickListen: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Filter", style = MaterialTheme.typography.headlineMedium)

        // Horizontal TextButtons
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(50)),

                onClick = { /* Handle click */ },
                shape = RoundedCornerShape(50) // Rounded corners
            ) {
                Text("Option 1")
            }

            TextButton(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(50)),
                onClick = { /* Handle click */ },
                shape = RoundedCornerShape(50) // Rounded corners
            ) {
                Text("Option 2")
            }
        }

        TextButton(
            onClick = onClickListen,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(50)),
            shape = RoundedCornerShape(50) // Rounded corners
        ) {
            Text("Select Region and District")
        }
        Button(
            onClick = { /* Handle Click */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(50) // Rounded corners
        ) {
            Text("Confirm")
        }
    }
}

@Composable
fun SearchToolbar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    searchQuery: String,
    onSearchTriggered: (String) -> Unit,
    onFilterClicked: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()

    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        SearchTextField(
            onSearchQueryChanged = onSearchQueryChanged,
            searchQuery = searchQuery,
            onSearchTriggered = onSearchTriggered
        )

        IconButton(onClick = onFilterClicked) {
            Icon(
                imageVector = Icons.Default.FilterList, // Use the appropriate filter icon
                contentDescription = "Filter", // Provide an appropriate content description
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

    }
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchTextField(
    onSearchQueryChanged: (String) -> Unit,
    searchQuery: String,
    onSearchTriggered: (String) -> Unit,
) {
    val focusRequest = remember {
        FocusRequester()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val onSearchExplicitlyTriggered = {
        keyboardController?.hide()
        onSearchTriggered(searchQuery)
    }

    TextField(
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = {
                    onSearchQueryChanged("")
                }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        onValueChange = {
            if (!it.contains("\n")) {
                onSearchQueryChanged(it)
            }
        },
        modifier = Modifier
            .wrapContentWidth()
            .padding(vertical = 16.dp)
            .focusRequester(focusRequest)
            .onKeyEvent {
                if (it.key == Key.Enter) {
                    onSearchExplicitlyTriggered()
                    true
                } else {
                    false
                }
            }
            .testTag("searchTextField"),
        shape = RoundedCornerShape(32.dp),
        value = searchQuery,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearchExplicitlyTriggered()
            },
        ),
        maxLines = 1,
        singleLine = true,
    )
    LaunchedEffect(Unit) {
        focusRequest.requestFocus()
    }

}