package org.don.onlineTrade.ui.main.home.search

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
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.category.CategoryItem
import org.don.onlineTrade.ui.filterCategory.ComposeLottieAnimation
import org.don.onlineTrade.ui.main.home.HomeViewModel
import org.don.onlineTrade.ui.main.home.ProductItem
import org.don.onlineTrade.ui.main.home.homeItems.ShimmerProductGrid
import org.don.onlineTrade.ui.main.home.search.filter.ActiveFilterChips
import org.don.onlineTrade.ui.main.home.search.filter.FilterClass
import org.don.onlineTrade.ui.main.home.search.filter.SearchToolbar
import org.don.onlineTrade.ui.main.home.search.filter.SuggestionsList
import org.don.onlineTrade.ui.map.MapScreenData
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

    val searchResultViewModel = hiltViewModel<SearchResultViewModel>()
    val searchViewModel = hiltViewModel<SearchViewModel>()
    val pagingItems = searchResultViewModel.searchResults.collectAsLazyPagingItems()
    val suggestions = searchViewModel.suggestions
    val queryText = searchViewModel.queryText

    var searchTextListener by remember { mutableStateOf(searchResultViewModel.searchText) }
    fun updateSearchText(value: String) {
        searchTextListener = value
        searchResultViewModel.searchText = value
    }
    var myFilter by searchResultViewModel::filter

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    val homeViewModel = hiltViewModel<HomeViewModel>()
    val homeState = homeViewModel.state.value

    val snackbarHostState = remember { SnackbarHostState() }
    val locationAndRadiusMsg = stringResource(R.string.location_updated)
    val locationOnlyMsg = stringResource(R.string.location_only_updated)
    val radiusOnlyMsg = stringResource(R.string.radius_only_updated)

    fun triggerSearch(query: String) {
        searchViewModel.clearSuggestions()
        searchViewModel.queryText = query
        val lat = searchViewModel.searchLat
        val lon = searchViewModel.searchLon
        val radius = searchViewModel.searchRadiusKm.coerceAtLeast(10)
        searchResultViewModel.search(
            SearchParams(
                query = query,
                lat = lat,
                lon = lon,
                radius = radius,
                categoryIds = myFilter.categoryIds,
                startDate = myFilter.titleTextFrom,
                endDate = myFilter.titleTextTo,
            )
        )
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
            myFilter = myFilter.copy(
                categoryIds = listOf(it.id.toLong()),
                categoryNames = listOf(it.title)
            )
            triggerSearch(searchTextListener)
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
                        updateSearchText(it)
                        if (!hasSearched) {
                            searchViewModel.onSuggestionQueryChanged(it)
                        }
                    },
                    onSearchTriggered = {
                        updateSearchText(it)
                        if (hasSearched) {
                            triggerSearch(it)
                        } else {
                            searchViewModel.fetchSuggestions(it)
                        }
                    },
                    searchQuery = searchTextListener,
                    onFilterClicked = {
                        showBottomSheet = true
                    },
                    onMapClick = onMapClick,
                    isFilterIconVisible = hasSearched && suggestions.isEmpty(),
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
                                categoryIds = listOf(suggestion.categoryId),
                                categoryNames = listOf(suggestion.name)
                            )
                            triggerSearch(searchTextListener)
                        }
                    )
                }

                // Filter chips
                if (hasSearched && suggestions.isEmpty()) {
                    ActiveFilterChips(
                        queryText = queryText,
                        radiusKm = searchViewModel.searchRadiusKm,
                        filter = myFilter,
                        onQueryClick = {
                            updateSearchText("")
                            triggerSearch("")
                        },
                        onRadiusClick = onMapClick,
                        onDateFromClick = { showBottomSheet = true },
                        onDateToClick = { showBottomSheet = true },
                        onPriceClick = { showBottomSheet = true },
                        onCategoryClick = {
                            if (myFilter.hasCategoryFilter) {
                                myFilter = myFilter.copy(categoryIds = emptyList(), categoryNames = emptyList())
                                triggerSearch(searchTextListener)
                            } else {
                                showCategoryDialog = true
                            }
                        }
                    )
                }

                if (!hasSearched) {
                    // Initial state: show nothing, suggestions are above
                } else if (isLoading && pagingItems.itemCount == 0) {
                    ShimmerProductGrid(
                        modifier = modifier
                            .padding(horizontal = MaterialTheme.spacing.dimen12Dp)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = modifier
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

        if (showCategoryDialog) {
            LaunchedEffect(Unit) {
                if (homeState.categoryList == null) {
                    homeViewModel.getAllCategories()
                }
            }
            CategoryPickerDialog(
                categories = homeState.categoryList,
                isLoading = homeState.isLoading,
                initialSelectedIds = myFilter.categoryIds.map { it.toInt() }.toSet(),
                onDismiss = { showCategoryDialog = false },
                onApply = { selectedItems ->
                    showCategoryDialog = false
                    myFilter = myFilter.copy(
                        categoryIds = selectedItems.map { it.id.toLong() },
                        categoryNames = selectedItems.map { it.title }
                    )
                    triggerSearch(searchTextListener)
                }
            )
        }
    }
}

