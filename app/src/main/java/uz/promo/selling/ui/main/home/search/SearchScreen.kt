package uz.promo.selling.ui.main.home.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.category.CategoryItem
import uz.promo.selling.ui.filterCategory.ComposeLottieAnimation
import uz.promo.selling.ui.main.home.ProductItem
import uz.promo.selling.ui.main.home.homeItems.ShimmerProductGrid
import uz.promo.selling.ui.main.home.search.filter.ActiveFilterChips
import uz.promo.selling.ui.main.home.search.filter.FilterClass
import uz.promo.selling.ui.main.home.search.filter.SearchHistoryList
import uz.promo.selling.ui.main.home.search.filter.SearchToolbar
import uz.promo.selling.ui.main.home.search.filter.SuggestionsList
import uz.promo.selling.ui.map.MapScreenData


@Composable
fun SearchRoute(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onItemClick: (Int) -> Unit,
    onMapClick: () -> Unit = {},
    onCategoryClick: () -> Unit = {},
    mapSearchData: MapScreenData? = null,
    categoryItem: CategoryItem? = null,
    preselectParentCategoryId: Int? = null,
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
        preselectParentCategoryId = preselectParentCategoryId,
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
    preselectParentCategoryId: Int? = null,
    searchBarModifier: Modifier = Modifier,
) {

    val searchResultViewModel = hiltViewModel<SearchResultViewModel>()
    val searchViewModel = hiltViewModel<SearchViewModel>()
    val pagingItems = searchResultViewModel.searchResults.collectAsLazyPagingItems()
    val suggestions = searchViewModel.suggestions
    val queryText = searchViewModel.queryText
    val history by searchViewModel.searchHistory.collectAsStateWithLifecycle()
    val isTyping = searchViewModel.isTyping

    var searchTextListener by remember { mutableStateOf(searchResultViewModel.searchText) }
    fun updateSearchText(value: String) {
        searchTextListener = value
        searchResultViewModel.searchText = value
    }
    var myFilter by searchResultViewModel::filter

    // AI no-results fallback: whether we've already let the AI reinterpret the
    // current empty result, and the banner query to show over AI-derived results.
    var aiTriedForEmpty by remember { mutableStateOf(false) }
    var aiBannerQuery by remember { mutableStateOf<String?>(null) }
    // True while myFilter carries AI-derived constraints; dropped on a fresh typed query.
    var aiAppliedFilters by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    var snackbarVisible by remember { mutableStateOf(false) }
    val locationAndRadiusMsg = stringResource(R.string.location_updated)
    val locationOnlyMsg = stringResource(R.string.location_only_updated)
    val radiusOnlyMsg = stringResource(R.string.radius_only_updated)

    LaunchedEffect(snackbarMessage) {
        if (snackbarMessage != null) {
            snackbarVisible = true
            delay(3000)
            snackbarVisible = false
            delay(400)
            snackbarMessage = null
        }
    }

    fun triggerSearch(query: String) {
        // A fresh user-initiated search resets the AI fallback bookkeeping.
        aiTriedForEmpty = false
        aiBannerQuery = null
        aiAppliedFilters = false
        searchViewModel.clearSuggestions()
        searchViewModel.queryText = query
        val lat = searchViewModel.searchLat
        val lon = searchViewModel.searchLon
        val radius = searchViewModel.searchRadiusKm
        searchResultViewModel.search(
            SearchParams(
                query = query,
                lat = lat,
                lon = lon,
                radius = radius,
                categoryIds = myFilter.categoryIds,
                startDate = myFilter.titleTextFrom,
                endDate = myFilter.titleTextTo,
                priceMin = myFilter.fromPrice,
                priceMax = myFilter.toPrice,
                sort = myFilter.sort,
            )
        )
    }

    // Load the full category tree up-front so AI search can resolve a parsed
    // categoryId to its leaf title (keeps categoryIds/categoryNames parallel).
    LaunchedEffect(Unit) {
        searchResultViewModel.loadAllCategories()
    }

    // Finds a leaf category by id in the loaded tree and returns its title, or
    // null when not loaded / not found (caller falls back to the query text).
    fun resolveCategoryName(id: Long): String? {
        val all = searchResultViewModel.allCategories ?: return null
        return all.firstNotNullOfOrNull { findCategoryTitle(it, id.toInt()) }
    }

    // Applies an AI resolution: reflects the understood category/price in the
    // chips and drives paging with the (possibly keyword-dropped) AI query,
    // leaving the visible search-bar text untouched.
    fun applyAiResolution(resolution: AiSearchResolution, originalQuery: String) {
        val categoryNames = resolution.parsed.categoryId?.let {
            listOf(resolveCategoryName(it) ?: (resolution.parsed.keywords ?: originalQuery))
        } ?: myFilter.categoryNames
        myFilter = myFilter.copy(
            categoryIds = resolution.categoryIds,
            categoryNames = categoryNames,
            fromPrice = resolution.priceMin,
            toPrice = resolution.priceMax,
            sort = resolution.sort,
        )
        aiBannerQuery = originalQuery
        aiAppliedFilters = true
        searchResultViewModel.search(
            SearchParams(
                query = resolution.effectiveQuery,
                lat = searchViewModel.searchLat,
                lon = searchViewModel.searchLon,
                radius = searchViewModel.searchRadiusKm,
                categoryIds = resolution.categoryIds,
                startDate = myFilter.titleTextFrom,
                endDate = myFilter.titleTextTo,
                priceMin = resolution.priceMin,
                priceMax = resolution.priceMax,
                sort = resolution.sort,
            )
        )
    }

    fun runAiSearch(query: String) {
        scope.launch {
            val resolution = searchResultViewModel.resolveAiSearch(
                query = query,
                lat = searchViewModel.searchLat,
                lon = searchViewModel.searchLon,
                radius = searchViewModel.searchRadiusKm,
                baseCategoryIds = myFilter.categoryIds,
                basePriceMin = myFilter.fromPrice,
                basePriceMax = myFilter.toPrice,
                baseSort = myFilter.sort,
            )
            if (resolution == null) {
                triggerSearch(query)
                return@launch
            }
            // Explicit AI: reflect the keywords in the field, then search.
            updateSearchText(resolution.parsed.keywords?.takeIf { it.isNotBlank() } ?: query)
            aiTriedForEmpty = true
            applyAiResolution(resolution, query)
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
                message?.let { msg -> snackbarMessage = msg }
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

    // Opened from a parent category on Home: preselect every leaf child of that
    // parent (same as ticking the parent in the category picker) and search.
    val parentPreselectApplied = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(preselectParentCategoryId) {
        if (preselectParentCategoryId != null && !parentPreselectApplied.value) {
            searchResultViewModel.loadAllCategories()
        }
    }
    LaunchedEffect(preselectParentCategoryId, searchResultViewModel.allCategories) {
        val parentId = preselectParentCategoryId ?: return@LaunchedEffect
        if (parentPreselectApplied.value) return@LaunchedEffect
        val parent = searchResultViewModel.allCategories
            ?.firstOrNull { it.id == parentId } ?: return@LaunchedEffect
        val leaves = collectLeafCategories(parent)
        if (leaves.isEmpty()) return@LaunchedEffect
        myFilter = myFilter.copy(
            categoryIds = leaves.map { it.id.toLong() },
            categoryNames = leaves.map { it.title }
        )
        parentPreselectApplied.value = true
        triggerSearch(searchTextListener)
    }

    val hasSearched = searchResultViewModel.hasSearched
    val isLoading = hasSearched && pagingItems.loadState.refresh is LoadState.Loading
    val isAppending = pagingItems.loadState.append is LoadState.Loading
    val isEmpty = hasSearched && pagingItems.itemCount == 0 && !isLoading

    // No-results fallback: when a normal search finds nothing, let the AI
    // reinterpret the query (parse → category/price) so the user isn't dead-ended.
    LaunchedEffect(isEmpty) {
        // Don't escalate when an explicit category is active — we must not override it.
        if (isEmpty && !aiTriedForEmpty && searchTextListener.length >= 2 &&
            !searchResultViewModel.isAiSearching && !myFilter.hasCategoryFilter) {
            aiTriedForEmpty = true
            val resolution = searchResultViewModel.resolveAiSearch(
                query = searchTextListener,
                lat = searchViewModel.searchLat,
                lon = searchViewModel.searchLon,
                radius = searchViewModel.searchRadiusKm,
                baseCategoryIds = myFilter.categoryIds,
                basePriceMin = myFilter.fromPrice,
                basePriceMax = myFilter.toPrice,
                baseSort = myFilter.sort,
            )
            if (resolution != null) {
                applyAiResolution(resolution, searchTextListener)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                            // A brand-new typed query drops AI-derived filters so they
                            // don't leak into an unrelated search.
                            if (aiAppliedFilters) {
                                myFilter = FilterClass()
                                aiAppliedFilters = false
                            }
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

                // AI search affordance: let the user run a natural-language search on
                // whatever they've typed (shown while typing, before a search runs).
                if (!hasSearched && searchTextListener.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            .clickable(enabled = !searchResultViewModel.isAiSearching) {
                                runAiSearch(searchTextListener)
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (searchResultViewModel.isAiSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        Text(
                            text = "✨ " + stringResource(R.string.ai_search_with_ai),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Show suggestions or history
                if (suggestions.isNotEmpty()) {
                    SuggestionsList(
                        query = queryText,
                        suggestions = suggestions,
                        onAllClick = {
                            searchViewModel.saveToHistory(searchTextListener, null, null)
                            triggerSearch(searchTextListener)
                        },
                        onSuggestionClick = { suggestion ->
                            searchViewModel.saveToHistory(
                                searchTextListener,
                                suggestion.name,
                                suggestion.categoryId
                            )
                            myFilter = myFilter.copy(
                                categoryIds = listOf(suggestion.categoryId),
                                categoryNames = listOf(suggestion.name)
                            )
                            triggerSearch(searchTextListener)
                        }
                    )
                } else if (!hasSearched && !isTyping && history.isNotEmpty()) {
                    SearchHistoryList(
                        history = history,
                        onHistoryClick = { item ->
                            updateSearchText(item.query)
                            if (item.categoryId != null && item.categoryName != null) {
                                myFilter = myFilter.copy(
                                    categoryIds = listOf(item.categoryId),
                                    categoryNames = listOf(item.categoryName)
                                )
                            }
                            triggerSearch(item.query)
                        },
                        onClearHistory = {
                            searchViewModel.clearHistory()
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

                if (aiBannerQuery != null && hasSearched && suggestions.isEmpty() && pagingItems.itemCount > 0) {
                    AiResultsBanner(aiBannerQuery!!)
                }

                if (!hasSearched && suggestions.isEmpty() && !isTyping && history.isEmpty() && searchViewModel.hasFetchedSuggestions && !searchViewModel.isLoadingSuggestions) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .width(64.dp)
                                    .height(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.no_posts_for_location),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium,
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = onMapClick,
                                shape = RoundedCornerShape(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = stringResource(R.string.update_location),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                } else if (!hasSearched) {
                    // Still loading suggestions or haven't fetched yet
                } else if (isLoading && pagingItems.itemCount == 0) {
                    ShimmerProductGrid(
                        modifier = modifier
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

        // Custom animated snackbar
        AnimatedVisibility(
            visible = snackbarVisible,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            ) + fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(14.dp))
                    .background(
                        color = Color(0xFF1A6B3C),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = snackbarMessage ?: "",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
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
                    allCategories = searchResultViewModel.allCategories,
                    isCategoriesLoading = searchResultViewModel.isCategoriesLoading,
                    onLoadCategories = { searchResultViewModel.loadAllCategories() },
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
                searchResultViewModel.loadAllCategories()
            }
            CategoryPickerDialog(
                categories = searchResultViewModel.allCategories,
                isLoading = searchResultViewModel.isCategoriesLoading,
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

/** Small banner shown over AI-derived results: "✨ AI matches for '…'". */
@Composable
private fun AiResultsBanner(query: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "✨ " + stringResource(R.string.ai_results_notice, query),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Flattens a category subtree to its leaf categories (the ones posts are actually
 * assigned to). Mirrors how the picker expands a parent selection to leaf ids.
 */
private fun collectLeafCategories(item: CategoryItem): List<CategoryItem> =
    if (item.children.isEmpty()) listOf(item)
    else item.children.flatMap { collectLeafCategories(it) }

/**
 * Depth-first search for a category by id in a subtree, returning its title
 * (used to resolve an AI-parsed categoryId to a display name).
 */
private fun findCategoryTitle(item: CategoryItem, id: Int): String? {
    if (item.id == id) return item.title
    return item.children.firstNotNullOfOrNull { findCategoryTitle(it, id) }
}

