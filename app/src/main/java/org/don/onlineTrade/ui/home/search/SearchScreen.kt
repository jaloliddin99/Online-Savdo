package org.don.onlineTrade.ui.home.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import org.don.onlineTrade.data.remote.models.category.Category
import org.don.onlineTrade.data.remote.models.category.CategoryItem
import org.don.onlineTrade.ui.categoriesList.CategoriesScreen
import org.don.onlineTrade.ui.filterCategory.ComposeLottieAnimation
import org.don.onlineTrade.ui.home.HomeScreenState
import org.don.onlineTrade.ui.home.HomeViewModel
import org.don.onlineTrade.ui.home.ProductItem
import org.don.onlineTrade.ui.home.homeItems.ShimmerProductGrid
import org.don.onlineTrade.ui.home.search.filter.ActiveFilterChips
import org.don.onlineTrade.ui.home.search.filter.FilterClass
import org.don.onlineTrade.ui.home.search.filter.SearchToolbar
import org.don.onlineTrade.ui.home.search.filter.SuggestionsList
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryPickerDialog(
    categories: Category?,
    isLoading: Boolean,
    initialSelectedIds: Set<Int>,
    onDismiss: () -> Unit,
    onApply: (List<CategoryItem>) -> Unit
) {
    val dialogSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val selectedIds = remember { mutableStateOf(initialSelectedIds.toMutableSet()) }

    fun toggleCategory(item: CategoryItem) {
        val ids = selectedIds.value.toMutableSet()
        if (ids.contains(item.id)) {
            ids.remove(item.id)
        } else {
            ids.add(item.id)
        }
        selectedIds.value = ids
    }

    fun toggleParent(item: CategoryItem) {
        val ids = selectedIds.value.toMutableSet()
        val childIds = item.children.map { it.id }.toSet()
        val allChildIds = collectAllChildIds(item)
        if (allChildIds.all { it in ids }) {
            ids.removeAll(allChildIds)
        } else {
            ids.addAll(allChildIds)
        }
        selectedIds.value = ids
    }

    fun collectSelectedItems(cats: List<CategoryItem>): List<CategoryItem> {
        val result = mutableListOf<CategoryItem>()
        for (cat in cats) {
            if (cat.id in selectedIds.value) result.add(cat)
            result.addAll(collectSelectedItems(cat.children))
        }
        return result
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = dialogSheetState,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.select_category_without),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                val count = selectedIds.value.size
                if (count > 0) {
                    Text(
                        text = "$count selected",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 12.dp, vertical = 8.dp
                    )
                ) {
                    categories?.let { cats ->
                        items(cats, key = { it.id }) { item ->
                            CheckboxCategoryCard(
                                item = item,
                                selectedIds = selectedIds.value,
                                onToggleLeaf = { toggleCategory(it) },
                                onToggleParent = { toggleParent(it) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            androidx.compose.material3.Button(
                onClick = {
                    onApply(collectSelectedItems(categories ?: emptyList()))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .height(48.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
            ) {
                Text(stringResource(R.string.txt_apply_filter))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun collectAllChildIds(item: CategoryItem): Set<Int> {
    val result = mutableSetOf<Int>()
    if (item.children.isEmpty()) {
        result.add(item.id)
    } else {
        for (child in item.children) {
            result.addAll(collectAllChildIds(child))
        }
    }
    return result
}

@Composable
private fun CheckboxCategoryCard(
    item: CategoryItem,
    selectedIds: Set<Int>,
    onToggleLeaf: (CategoryItem) -> Unit,
    onToggleParent: (CategoryItem) -> Unit
) {
    val hasChildren = item.children.isNotEmpty()
    var isExpanded by remember { mutableStateOf(false) }
    val allChildIds = remember(item) { collectAllChildIds(item) }
    val allSelected = allChildIds.all { it in selectedIds }
    val someSelected = !allSelected && allChildIds.any { it in selectedIds }

    val rotationAngle by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = androidx.compose.animation.core.tween(300),
        label = "chevron"
    )

    androidx.compose.material3.Card(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (hasChildren) isExpanded = !isExpanded
                        else onToggleLeaf(item)
                    }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.TriStateCheckbox(
                    state = when {
                        allSelected -> androidx.compose.ui.state.ToggleableState.On
                        someSelected -> androidx.compose.ui.state.ToggleableState.Indeterminate
                        else -> androidx.compose.ui.state.ToggleableState.Off
                    },
                    onClick = {
                        if (hasChildren) onToggleParent(item)
                        else onToggleLeaf(item)
                    }
                )

                if (item.image != null) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        val url = "${org.don.onlineTrade.BuildConfig.BASE_URL}categories/image/${item.image}"
                        coil.compose.AsyncImage(
                            modifier = Modifier.size(22.dp),
                            model = url,
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (hasChildren) {
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(rotationAngle)
                    )
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = isExpanded,
                enter = androidx.compose.animation.expandVertically(
                    animationSpec = androidx.compose.animation.core.tween(300)
                ),
                exit = androidx.compose.animation.shrinkVertically(
                    animationSpec = androidx.compose.animation.core.tween(300)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    item.children.forEachIndexed { index, child ->
                        CheckboxChildItem(
                            item = child,
                            selectedIds = selectedIds,
                            onToggleLeaf = onToggleLeaf,
                            onToggleParent = onToggleParent
                        )
                        if (index < item.children.lastIndex) {
                            androidx.compose.material3.HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 24.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckboxChildItem(
    item: CategoryItem,
    selectedIds: Set<Int>,
    onToggleLeaf: (CategoryItem) -> Unit,
    onToggleParent: (CategoryItem) -> Unit
) {
    val hasChildren = item.children.isNotEmpty()
    var isExpanded by remember { mutableStateOf(false) }
    val allChildIds = remember(item) { collectAllChildIds(item) }
    val allSelected = allChildIds.all { it in selectedIds }
    val someSelected = !allSelected && allChildIds.any { it in selectedIds }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (hasChildren) isExpanded = !isExpanded
                    else onToggleLeaf(item)
                }
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (hasChildren) {
                androidx.compose.material3.TriStateCheckbox(
                    state = when {
                        allSelected -> androidx.compose.ui.state.ToggleableState.On
                        someSelected -> androidx.compose.ui.state.ToggleableState.Indeterminate
                        else -> androidx.compose.ui.state.ToggleableState.Off
                    },
                    onClick = { onToggleParent(item) }
                )
            } else {
                androidx.compose.material3.Checkbox(
                    checked = item.id in selectedIds,
                    onCheckedChange = { onToggleLeaf(item) }
                )
            }

            if (item.image != null) {
                val url = "${org.don.onlineTrade.BuildConfig.BASE_URL}categories/image/${item.image}"
                coil.compose.AsyncImage(
                    modifier = Modifier.size(18.dp),
                    model = url,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            if (hasChildren) {
                val angle by androidx.compose.animation.core.animateFloatAsState(
                    targetValue = if (isExpanded) 180f else 0f,
                    label = "child_chevron"
                )
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(angle)
                )
            }
        }

        androidx.compose.animation.AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
            ) {
                item.children.forEachIndexed { index, grandChild ->
                    CheckboxChildItem(
                        item = grandChild,
                        selectedIds = selectedIds,
                        onToggleLeaf = onToggleLeaf,
                        onToggleParent = onToggleParent
                    )
                    if (index < item.children.lastIndex) {
                        androidx.compose.material3.HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}
