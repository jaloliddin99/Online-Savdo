package org.don.onlineTrade.ui.home.search.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.unit.dp
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.searchSuggestion.CategorySuggestion
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing

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
        add(
            ChipData(
                label = filter.categoryName ?: stringResource(R.string.category),
                applied = isCategoryApplied,
                onClick = onCategoryClick
            )
        )
        add(
            ChipData(
                label = if (radiusKm > 0) "$radiusKm km" else stringResource(R.string.location),
                applied = radiusKm > 0,
                onClick = onRadiusClick
            )
        )
        add(ChipData(label = priceLabel, applied = isPriceApplied, onClick = onPriceClick))
        add(
            ChipData(
                label = filter.titleTextFrom ?: stringResource(R.string.from),
                applied = filter.titleTextFrom != null,
                onClick = onDateFromClick
            )
        )
        add(
            ChipData(
                label = filter.titleTextTo ?: stringResource(R.string.to),
                applied = filter.titleTextTo != null,
                onClick = onDateToClick
            )
        )
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
    val fromPrice: Int? = null,
    val toPrice: Int? = null,
    val categoryId: Long? = null,
    val categoryName: String? = null
)

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
    searchBarModifier: Modifier = Modifier,
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
            modifier = Modifier.weight(1f),
            onMapClick = onMapClick,
            autoFocus = autoFocus,
            searchBarModifier = searchBarModifier,
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
    searchBarModifier: Modifier = Modifier,
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
            .then(searchBarModifier)
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

