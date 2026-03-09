package org.don.onlineTrade.ui.home.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import org.don.onlineTrade.R

import org.don.onlineTrade.data.remote.models.region.District
import org.don.onlineTrade.data.remote.models.region.RegionDistrict
import org.don.onlineTrade.data.remote.models.searchSuggestion.CategorySuggestion
import org.don.onlineTrade.data.remote.models.category.CategoryItem
import org.don.onlineTrade.ui.filterCategory.ComposeLottieAnimation
import org.don.onlineTrade.ui.home.HomeViewModel
import org.don.onlineTrade.ui.home.ProductItem
import org.don.onlineTrade.ui.home.ShimmerProductGrid
import org.don.onlineTrade.ui.map.MapScreenData
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.convertLongToDateString


@Composable
fun SearchRoute(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onItemClick: (Int) -> Unit,
    onMapClick: () -> Unit = {},
    onCategoryClick: () -> Unit = {},
    mapSearchData: MapScreenData? = null,
    categoryItem: CategoryItem? = null,
) {

    SearchScreen(
        modifier = modifier,
        onBackClick = onBackClick,
        onItemClick = onItemClick,
        onMapClick = onMapClick,
        onCategoryClick = onCategoryClick,
        mapSearchData = mapSearchData,
        categoryItem = categoryItem,
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
) {

    var searchTextListener by remember {
        mutableStateOf("")
    }

    val homeViewModel = hiltViewModel<HomeViewModel>()
    val searchViewModel = hiltViewModel<SearchViewModel>()
    val pagerState = homeViewModel.pagerState
    val suggestions = searchViewModel.suggestions
    val queryText = searchViewModel.queryText

    val scrollState = rememberLazyGridState()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var myFilter by remember {
        mutableStateOf(FilterClass())
    }

    // Update location data when map data arrives
    LaunchedEffect(mapSearchData) {
        mapSearchData?.let {
            if (it.lat != null && it.lon != null) {
                searchViewModel.updateSearchLocation(it.lat, it.lon, it.radiusKm.toInt())
            }
        }
    }


    fun triggerSearch(query: String, categoryId: Long? = null) {
        searchViewModel.clearSuggestions()
        homeViewModel.resetPager()
        val catId = categoryId ?: myFilter.categoryId
        homeViewModel.loadNextItems(
            query = query,
            categoryId = catId?.toInt(),
            minPrice = myFilter.fromPrice,
            maxPrice = myFilter.toPrice,
            startDate = myFilter.titleTextFrom,
            endDate = myFilter.titleTextTo,
            regionId = myFilter.regionId,
            districtId = myFilter.districtId
        )
        if (categoryId != null) {
            myFilter = myFilter.copy(categoryId = categoryId)
        }
    }

    // Handle category selection from CategoriesRoute
    LaunchedEffect(categoryItem) {
        categoryItem?.let {
            myFilter = myFilter.copy(categoryId = it.id.toLong(), categoryName = it.title)
            triggerSearch(searchTextListener, it.id.toLong())
        }
    }

    Box {
        Column(modifier = modifier) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
            SearchToolbar(
                onBackClick = onBackClick,
                onSearchQueryChanged = {
                    searchTextListener = it
                    searchViewModel.onSuggestionQueryChanged(it)
                    homeViewModel.resetPager()
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
                isFilterIconVisible = suggestions.isEmpty() && pagerState.items.isNotEmpty()
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
            if (suggestions.isEmpty() && (pagerState.items.isNotEmpty() || pagerState.isLoading)) {
                ActiveFilterChips(
                    queryText = queryText,
                    radiusKm = searchViewModel.searchRadiusKm,
                    filter = myFilter,
                    onQueryClick = {
                        searchTextListener = ""
                        homeViewModel.resetPager()
                        searchViewModel.fetchSuggestions("")
                    },
                    onRadiusClick = onMapClick,
                    onDateFromClick = { showBottomSheet = true },
                    onDateToClick = { showBottomSheet = true },
                    onRegionClick = { showBottomSheet = true },
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

            if (pagerState.isLoading && pagerState.items.isEmpty() && pagerState.page == 0) {
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

                    items(pagerState.items.size) { i ->
                        val item = pagerState.items[i]
                        LaunchedEffect(scrollState) {
                            if (i >= pagerState.items.size - 1 && !pagerState.endReached && !pagerState.isLoading) {
                                homeViewModel.loadNextItems(
                                    query = searchTextListener,
                                    categoryId = myFilter.categoryId?.toInt(),
                                    minPrice = myFilter.fromPrice,
                                    maxPrice = myFilter.toPrice,
                                    startDate = myFilter.titleTextFrom,
                                    endDate = myFilter.titleTextTo,
                                    regionId = myFilter.regionId,
                                    districtId = myFilter.districtId
                                )
                            }
                        }
                        ProductItem(
                            item,
                            onItemClicked = onItemClick,
                            onItemLongLicked = {}
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
                        if (!sheetState.isVisible) {
                            myFilter = filter
                            showBottomSheet = false
                            homeViewModel.resetPager()
                            homeViewModel.loadNextItems(
                                query = searchTextListener,
                                categoryId = filter.categoryId?.toInt(),
                                minPrice = filter.fromPrice,
                                maxPrice = filter.toPrice,
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

@Composable
fun SuggestionsList(
    query: String,
    suggestions: List<CategorySuggestion>,
    onAllClick: () -> Unit,
    onSuggestionClick: (CategorySuggestion) -> Unit
) {
    val totalCount = suggestions.sumOf { it.count }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.dimen16Dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(MaterialTheme.spacing.dimen12Dp)
            )
            .padding(horizontal = MaterialTheme.spacing.dimen12Dp)
    ) {
        // "All" item
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAllClick() }
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 12.dp)
            )
            if (query.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.all_posts),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = robotoFontFamily,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = query,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(id = R.string.all_posts),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                    )
                }
            }
            Text(
                text = "$totalCount",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        HorizontalDivider()

        suggestions.forEachIndexed { index, suggestion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSuggestionClick(suggestion) }
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 12.dp)
                )
                if (query.isEmpty()) {
                    Text(
                        text = suggestion.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = robotoFontFamily,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = query,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = stringResource(R.string.search_screen_in, suggestion.name),
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight.Normal,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                        )

                    }
                }
                Text(
                    text = "${suggestion.count}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (index != suggestions.lastIndex) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun ActiveFilterChips(
    queryText: String,
    radiusKm: Int,
    filter: FilterClass,
    onQueryClick: () -> Unit,
    onRadiusClick: () -> Unit,
    onDateFromClick: () -> Unit,
    onDateToClick: () -> Unit,
    onRegionClick: () -> Unit,
    onPriceClick: () -> Unit,
    onCategoryClick: () -> Unit
) {
    data class ChipData(
        val label: String,
        val applied: Boolean,
        val onClick: () -> Unit
    )

    val isCategoryApplied = filter.categoryId != null
    val isPriceApplied = filter.fromPrice != null || filter.toPrice != null
    val priceLabel = if (isPriceApplied) {
        buildString {
            filter.fromPrice?.let { append(it) }
            append(" - ")
            filter.toPrice?.let { append(it) }
        }
    } else stringResource(R.string.price)

    val chips = buildList {
        if (queryText.isNotEmpty()) {
            add(ChipData(label = queryText, applied = true, onClick = onQueryClick))
        }
        add(ChipData(label = filter.categoryName ?: stringResource(R.string.category), applied = isCategoryApplied, onClick = onCategoryClick))
        add(ChipData(label = if (radiusKm > 0) "$radiusKm km" else stringResource(R.string.location), applied = radiusKm > 0, onClick = onRadiusClick))
        add(ChipData(label = priceLabel, applied = isPriceApplied, onClick = onPriceClick))
        add(ChipData(label = filter.titleTextFrom ?: stringResource(R.string.from), applied = filter.titleTextFrom != null, onClick = onDateFromClick))
        add(ChipData(label = filter.titleTextTo ?: stringResource(R.string.to), applied = filter.titleTextTo != null, onClick = onDateToClick))
        add(ChipData(label = stringResource(R.string.regions), applied = filter.regionId != -1, onClick = onRegionClick))
    }.sortedByDescending { it.applied }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = MaterialTheme.spacing.dimen16Dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chips.forEach { chip ->
            FilterChip(
                label = chip.label,
                applied = chip.applied,
                onClick = chip.onClick
            )
        }
    }
}

@Composable
fun FilterChip(
    label: String,
    applied: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .then(
                if (applied) {
                    Modifier.background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(50)
                    )
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(50)
                    )
                }
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = if (applied) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface
            )
            if (applied) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

data class FilterClass(
    val titleTextFrom: String? = null,
    val titleTextTo: String? = null,
    val regionId: Int = -1,
    val districtId: Int = -1,
    val fromPrice: Int? = null,
    val toPrice: Int? = null,
    val categoryId: Long? = null,
    val categoryName: String? = null
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
    var fromPrice by remember { mutableStateOf("") }
    var toPrice by remember { mutableStateOf("") }

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

        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = fromPrice,
                onValueChange = { fromPrice = it.filter { c -> c.isDigit() } },
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(R.string.from)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(50),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
            TextField(
                value = toPrice,
                onValueChange = { toPrice = it.filter { c -> c.isDigit() } },
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(R.string.to)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(50),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
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
                        titleTextFrom = titleTextFrom,
                        titleTextTo = titleTextTo,
                        regionId = (regionId?.id ?: -1),
                        districtId = (districtId?.id ?: -1),
                        fromPrice = fromPrice.toIntOrNull(),
                        toPrice = toPrice.toIntOrNull()
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
    onFilterClicked: (() -> Unit)? = null,
    onMapClick: (() -> Unit)? = null,
    autoFocus: Boolean = true,
    isFilterIconVisible: Boolean = true,
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
            modifier = modifier.weight(1f),
            onMapClick = onMapClick,
            autoFocus = autoFocus
        )

        if (onFilterClicked != null && isFilterIconVisible) {
            IconButton(onClick = onFilterClicked) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        } else {
            Spacer(modifier.width(16.dp))
        }

    }
}

@Composable
fun SearchTextField(
    modifier: Modifier,
    onSearchQueryChanged: (String) -> Unit,
    searchQuery: String,
    onSearchTriggered: (String) -> Unit,
    onMapClick: (() -> Unit)? = null,
    autoFocus: Boolean = true,
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
            Row {
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
                if (onMapClick != null && searchQuery.isEmpty()) {
                    IconButton(onClick = onMapClick) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
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
    if (autoFocus) {
        LaunchedEffect(Unit) {
            focusRequest.requestFocus()
        }
    }

}