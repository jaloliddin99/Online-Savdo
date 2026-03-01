package org.don.onlineTrade.ui.home.search

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import org.don.onlineTrade.R

import org.don.onlineTrade.data.remote.models.region.District
import org.don.onlineTrade.data.remote.models.region.RegionDistrict
import org.don.onlineTrade.ui.filterCategory.ComposeLottieAnimation
import org.don.onlineTrade.ui.home.HomeViewModel
import org.don.onlineTrade.ui.home.ProductItem
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.convertLongToDateString


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
    var myFilter by remember {
        mutableStateOf(FilterClass())
    }


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
                        query = searchTextListener,
                        startDate = myFilter.titleTextFrom,
                        endDate = myFilter.titleTextTo,
                        regionId = myFilter.regionId,
                        districtId = myFilter.districtId
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
                                query = searchTextListener,
                                startDate = myFilter.titleTextFrom,
                                endDate = myFilter.titleTextTo,
                                regionId = myFilter.regionId,
                                districtId = myFilter.districtId
                            )
                        }
                    }
                    ProductItem(
                        item,
                        onItemClicked = onItemClick
                        ,onItemLongLicked = {}
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
            sheetState = sheetState
        ) {
            BottomSheetContent(
                onClickListen = { filter ->
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible){
                            myFilter = filter
                            showBottomSheet = false
                            homeViewModel.resetPager()
                            homeViewModel.loadNextItems(
                                query = searchTextListener,
                                startDate = filter.titleTextFrom,
                                endDate = filter.titleTextTo,
                                regionId = filter.regionId,
                                districtId = filter.districtId
                            )
                        }
                    }
                }
            )
        }
    }


}

data class FilterClass(
    val titleTextFrom: String? = null,
    val titleTextTo: String? = null,
    val regionId: Int = -1,
    val districtId: Int = -1
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContent(
    onClickListen: (FilterClass) -> Unit
) {

    val datePickerState = rememberDatePickerState()
    val showDialog = rememberSaveable { mutableIntStateOf(0) }
    var titleTextFrom by remember {
        mutableStateOf<String?>(null)
    }
    var titleTextTo by remember {
        mutableStateOf<String?>(null)
    }
    val showRegionDistrictDialog = remember {
        mutableStateOf(false)
    }
    var regionId by remember {
        mutableStateOf<RegionDistrict?>(null)
    }
    var districtId by remember {
        mutableStateOf<District?>(null)
    }

    if (showRegionDistrictDialog.value) {
        ShowRegionsDialog(onDismissRequest = {
            showRegionDistrictDialog.value = false
        }, onSelectRequest = { region, district ->
            regionId = region
            districtId = district
            showRegionDistrictDialog.value = false
        })
    }
    if (showDialog.intValue != 0) {
        DatePickerDialog(
            onDismissRequest = { showDialog.intValue = 0 },
            confirmButton = {
                TextButton(onClick = { showDialog.intValue = 0 }) {
                    datePickerState.selectedDateMillis?.let {
                        val date = convertLongToDateString(it)
                        if (showDialog.intValue == 1)
                            titleTextFrom = date
                        else titleTextTo = date
                    }
                    Text("Ok")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.intValue = 0 }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text("Filter", style = MaterialTheme.typography.headlineMedium)

        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(50)),

                onClick = {
                    showDialog.intValue = 1
                },
                shape = RoundedCornerShape(50) // Rounded corners
            ) {
                Text(titleTextFrom ?: stringResource(id = R.string.from))
            }

            TextButton(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(50)),
                onClick = { showDialog.intValue = 2 },
                shape = RoundedCornerShape(50) // Rounded corners
            ) {
                Text(titleTextTo ?: stringResource(id = R.string.to))
            }
        }

        TextButton(
            onClick = {
                showRegionDistrictDialog.value = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(50)),
            shape = RoundedCornerShape(50)
        ) {
            Text(regionId?.name ?: stringResource(id = R.string.txt_select_region_and_district))
        }

        Button(
            onClick = {
                onClickListen.invoke(
                    FilterClass(
                        titleTextFrom,
                        titleTextTo,
                        (regionId?.id ?: -1),
                        (districtId?.id ?: -1)
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(stringResource(id = R.string.txt_apply_filter))
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen24Dp))
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
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        SearchTextField(
            onSearchQueryChanged = onSearchQueryChanged,
            searchQuery = searchQuery,
            onSearchTriggered = onSearchTriggered,
            modifier = modifier.weight(1f)
        )

        IconButton(onClick = onFilterClicked) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

    }
}

@Composable
fun SearchTextField(
    modifier: Modifier,
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
        colors = TextFieldDefaults.colors(
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
        modifier = modifier
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