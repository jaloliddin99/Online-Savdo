package org.don.onlineTrade.ui.home.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.category.CategoryItem
import org.don.onlineTrade.data.remote.models.searchSuggestion.CategorySuggestion
import org.don.onlineTrade.ui.filterCategory.ComposeLottieAnimation
import org.don.onlineTrade.ui.home.ProductItem
import org.don.onlineTrade.ui.home.homeItems.ShimmerProductGrid
import org.don.onlineTrade.ui.home.search.filter.ActiveFilterChips
import org.don.onlineTrade.ui.home.search.filter.FilterClass
import org.don.onlineTrade.ui.home.search.filter.SearchToolbar
import org.don.onlineTrade.ui.home.search.filter.SuggestionsList
import org.don.onlineTrade.ui.map.MapScreenData
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing


@Composable
fun SearchRoute(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onItemClick: (Int) -> Unit,
    onMapClick: () -> Unit = {},
    onCategoryClick: () -> Unit = {},
    mapSearchData: MapScreenData? = null,
    categoryItem: CategoryItem? = null,
    searchBarModifier: Modifier = Modifier,
) {
    SearchScreen(
        modifier = modifier,
        onBackClick = onBackClick,
        onItemClick = onItemClick,
        onMapClick = onMapClick,
        onCategoryClick = onCategoryClick,
        mapSearchData = mapSearchData,
        categoryItem = categoryItem,
        searchBarModifier = searchBarModifier,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onItemClick: (Int) -> Unit = {},
    onMapClick: () -> Unit = {},
    onCategoryClick: () -> Unit = {},
    mapSearchData: MapScreenData? = null,
    categoryItem: CategoryItem? = null,
    searchBarModifier: Modifier = Modifier,
) {

    var searchTextListener by remember { mutableStateOf("") }

    val searchResultViewModel = hiltViewModel<SearchResultViewModel>()
    val searchViewModel = hiltViewModel<SearchViewModel>()
    val pagingItems = searchResultViewModel.searchResults.collectAsLazyPagingItems()
    val suggestions = searchViewModel.suggestions
    val queryText = searchViewModel.queryText

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var myFilter by remember { mutableStateOf(FilterClass()) }

    val snackbarHostState = remember { SnackbarHostState() }
    val locationAndRadiusMsg = stringResource(R.string.location_updated)
    val locationOnlyMsg = stringResource(R.string.location_only_updated)
    val radiusOnlyMsg = stringResource(R.string.radius_only_updated)

    fun triggerSearch(query: String, categoryId: Long? = null) {
        searchViewModel.clearSuggestions()
        val catId = categoryId ?: myFilter.categoryId
        val lat = searchViewModel.searchLat
        val lon = searchViewModel.searchLon
        val radius = searchViewModel.searchRadiusKm.coerceAtLeast(10)
        searchResultViewModel.search(
            SearchParams(
                query = query,
                lat = lat,
                lon = lon,
                radius = radius,
                categoryId = catId,
                startDate = myFilter.titleTextFrom,
                endDate = myFilter.titleTextTo,
            )
        )
        if (categoryId != null) {
            myFilter = myFilter.copy(categoryId = categoryId)
        }
    }

    // Update location data when map data arrives and trigger search
    LaunchedEffect(mapSearchData) {
        mapSearchData?.let {
            if (it.lat != null && it.lon != null) {
                val locationChanged =
                    it.lat != searchViewModel.searchLat || it.lon != searchViewModel.searchLon
                val radiusChanged = it.radiusKm != searchViewModel.searchRadiusKm
                searchViewModel.updateSearchLocation(it.lat, it.lon, it.radiusKm)
                if (locationChanged || radiusChanged) {
                    triggerSearch(searchTextListener)
                }
                val message = when {
                    locationChanged && radiusChanged -> locationAndRadiusMsg
                    locationChanged -> locationOnlyMsg
                    radiusChanged -> radiusOnlyMsg
                    else -> null
                }
                message?.let { msg -> snackbarHostState.showSnackbar(msg) }
            }
        }
    }

    LaunchedEffect(categoryItem) {
        categoryItem?.let {
            myFilter = myFilter.copy(categoryId = it.id.toLong(), categoryName = it.title)
            triggerSearch(searchTextListener, it.id.toLong())
        }
    }

    val hasSearched = searchResultViewModel.hasSearched
    val isLoading = hasSearched && pagingItems.loadState.refresh is LoadState.Loading
    val isAppending = pagingItems.loadState.append is LoadState.Loading
    val isEmpty = hasSearched && pagingItems.itemCount == 0 && !isLoading

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = modifier) {
                Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
                SearchToolbar(
                    onBackClick = onBackClick,
                    onSearchQueryChanged = {
                        searchTextListener = it
                        searchViewModel.onSuggestionQueryChanged(it)
                    },
                    onSearchTriggered = {
                        searchTextListener = it
                        triggerSearch(it)
                    },
                    searchQuery = searchTextListener,
                    onFilterClicked = {
                        showBottomSheet = true
                    },
                    onMapClick = onMapClick,
                    isFilterIconVisible = suggestions.isEmpty() && pagingItems.itemCount > 0,
                    searchBarModifier = searchBarModifier,
                )

                // Show suggestions if available
                if (suggestions.isNotEmpty()) {
                    SuggestionsList(
                        query = queryText,
                        suggestions = suggestions,
                        onAllClick = {
                            triggerSearch(searchTextListener)
                        },
                        onSuggestionClick = { suggestion ->
                            myFilter = myFilter.copy(
                                categoryId = suggestion.categoryId,
                                categoryName = suggestion.name
                            )
                            triggerSearch(searchTextListener, suggestion.categoryId)
                        }
                    )
                }

                // Filter chips
                if (hasSearched && suggestions.isEmpty() && (pagingItems.itemCount > 0 || isLoading)) {
                    ActiveFilterChips(
                        queryText = queryText,
                        radiusKm = searchViewModel.searchRadiusKm,
                        filter = myFilter,
                        onQueryClick = {
                            searchTextListener = ""
                            searchViewModel.fetchSuggestions("")
                        },
                        onRadiusClick = onMapClick,
                        onDateFromClick = { showBottomSheet = true },
                        onDateToClick = { showBottomSheet = true },
                        onPriceClick = { showBottomSheet = true },
                        onCategoryClick = {
                            if (myFilter.categoryId != null) {
                                myFilter = myFilter.copy(categoryId = null, categoryName = null)
                                triggerSearch(searchTextListener)
                            } else {
                                onCategoryClick()
                            }
                        }
                    )
                }

                if (!hasSearched) {
                    // Initial state: show nothing, suggestions are above
                } else if (isLoading && pagingItems.itemCount == 0) {
                    ShimmerProductGrid(
                        modifier = modifier
                            .padding(end = MaterialTheme.spacing.dimen16Dp)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = modifier
                            .padding(end = MaterialTheme.spacing.dimen16Dp)
                            .fillMaxSize(),
                    ) {
                        items(pagingItems.itemCount) { i ->
                            pagingItems[i]?.let { item ->
                                ProductItem(
                                    item,
                                    onItemClicked = onItemClick,
                                    onItemLongLicked = {}
                                )
                            }
                        }
                        item(span = { GridItemSpan(2) }) {
                            if (isAppending) {
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

            }
            if (isEmpty && !isLoading) {
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
                FilterBottomSheetContent(
                    currentRadiusKm = searchViewModel.searchRadiusKm,
                    categories = searchResultViewModel.categories,
                    initialFilter = myFilter,
                    onApply = { filter ->
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                myFilter = filter
                                showBottomSheet = false
                                triggerSearch(searchTextListener)
                            }
                        }
                    },
                    onReset = {
                        myFilter = FilterClass()
                    },
                    onLocationClick = {
                        showBottomSheet = false
                        onMapClick()
                    }
                )
            }
        }
    }
}
