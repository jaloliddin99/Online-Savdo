package uz.promo.selling.ui.main.home.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.category.Category
import uz.promo.selling.ui.main.home.search.filter.FilterClass
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.ui.theme.spacing
import uz.promo.selling.utils.convertLongToDateString
import uz.promo.selling.utils.convertLongToDisplayDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheetContent(
    currentRadiusKm: Int,
    allCategories: Category?,
    isCategoriesLoading: Boolean,
    onLoadCategories: () -> Unit,
    initialFilter: FilterClass,
    onApply: (FilterClass) -> Unit,
    onReset: () -> Unit,
    onLocationClick: () -> Unit,
) {
    var dateFromApi by remember { mutableStateOf(initialFilter.titleTextFrom) }
    var dateToApi by remember { mutableStateOf(initialFilter.titleTextTo) }
    var dateFromDisplay by remember { mutableStateOf(initialFilter.displayDateFrom) }
    var dateToDisplay by remember { mutableStateOf(initialFilter.displayDateTo) }

    var fromPrice by remember { mutableStateOf(initialFilter.fromPrice?.toString() ?: "") }
    var toPrice by remember { mutableStateOf(initialFilter.toPrice?.toString() ?: "") }
    var selectedCategoryIds by remember { mutableStateOf(initialFilter.categoryIds) }
    var selectedCategoryNames by remember { mutableStateOf(initialFilter.categoryNames) }
    var selectedSort by remember { mutableStateOf(initialFilter.sort) }

    val datePickerState = rememberDatePickerState()
    val showDatePicker = rememberSaveable { mutableIntStateOf(0) }

    if (showDatePicker.intValue != 0) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker.intValue = 0 },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val apiDate = convertLongToDateString(millis)
                        val displayDate = convertLongToDisplayDate(millis)
                        if (showDatePicker.intValue == 1) {
                            dateFromApi = apiDate
                            dateFromDisplay = displayDate
                        } else {
                            dateToApi = apiDate
                            dateToDisplay = displayDate
                        }
                    }
                    showDatePicker.intValue = 0
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker.intValue = 0 }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val hasAnyFilter = dateFromApi != null || dateToApi != null ||
            fromPrice.isNotBlank() || toPrice.isNotBlank() || selectedCategoryIds.isNotEmpty() ||
            selectedSort != null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        // ── Header ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.filter_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                fontFamily = robotoFontFamily
            )
            if (hasAnyFilter) {
                TextButton(onClick = {
                    dateFromApi = null
                    dateToApi = null
                    dateFromDisplay = null
                    dateToDisplay = null
                    fromPrice = ""
                    toPrice = ""
                    selectedCategoryIds = emptyList()
                    selectedCategoryNames = emptyList()
                    selectedSort = null
                    onReset()
                }) {
                    Text(stringResource(R.string.filter_reset_all), color = MaterialTheme.colorScheme.error)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Section 1: Location & Radius ──
        FilterSectionHeader(
            icon = Icons.Filled.MyLocation,
            title = stringResource(R.string.location)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .clickable { onLocationClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (currentRadiusKm > 0) stringResource(R.string.filter_km_radius, currentRadiusKm)
                        else stringResource(R.string.filter_no_location),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Text(
                        text = stringResource(R.string.filter_tap_change_location),
                        fontFamily = robotoFontFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Filled.MyLocation,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(20.dp))

        // ── Section 2: Categories ──
        FilterSectionHeader(
            icon = Icons.Filled.Check,
            title = stringResource(R.string.category)
        )
        Spacer(modifier = Modifier.height(10.dp))

        var showCategoryPicker by remember { mutableStateOf(false) }

        // "Select Category" button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .clickable {
                    onLoadCategories()
                    showCategoryPicker = true
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.select_category_without),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            if (selectedCategoryIds.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${selectedCategoryIds.size}",
                        fontSize = 12.sp,
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        // Show selected category names as chips
        if (selectedCategoryNames.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedCategoryNames.forEachIndexed { index, name ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable {
                                selectedCategoryIds = selectedCategoryIds.toMutableList().also { it.removeAt(index) }
                                selectedCategoryNames = selectedCategoryNames.toMutableList().also { it.removeAt(index) }
                            }
                            .padding(start = 12.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            fontFamily = robotoFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
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

        if (showCategoryPicker) {
            CategoryPickerDialog(
                categories = allCategories,
                isLoading = isCategoriesLoading,
                initialSelectedIds = selectedCategoryIds.map { it.toInt() }.toSet(),
                onDismiss = { showCategoryPicker = false },
                onApply = { selectedItems ->
                    showCategoryPicker = false
                    selectedCategoryIds = selectedItems.map { it.id.toLong() }
                    selectedCategoryNames = selectedItems.map { it.title }
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(20.dp))

        // ── Section 3: Price Range ──
        FilterSectionHeader(
            icon = Icons.Filled.Check,
            title = stringResource(R.string.price)
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterTextField(
                value = fromPrice,
                onValueChange = { fromPrice = it.filter { c -> c.isDigit() } },
                placeholder = stringResource(R.string.from),
                modifier = Modifier.weight(1f)
            )
            FilterTextField(
                value = toPrice,
                onValueChange = { toPrice = it.filter { c -> c.isDigit() } },
                placeholder = stringResource(R.string.to),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(20.dp))

        // ── Section: Sorting ──
        FilterSectionHeader(
            icon = Icons.AutoMirrored.Filled.Sort,
            title = stringResource(R.string.sort_by)
        )
        Spacer(modifier = Modifier.height(10.dp))

        val sortOptions = listOf(
            null to stringResource(R.string.sort_newest),
            "price_asc" to stringResource(R.string.sort_price_low),
            "price_desc" to stringResource(R.string.sort_price_high),
            "popular" to stringResource(R.string.sort_popular),
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sortOptions.forEach { (value, label) ->
                val isSelected = selectedSort == value
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                        .clickable { selectedSort = value }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        fontFamily = robotoFontFamily,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(20.dp))

        // ── Section 4: Date Range ──
        FilterSectionHeader(
            icon = Icons.Filled.CalendarMonth,
            title = stringResource(R.string.filter_date_range)
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DateChip(
                label = dateFromDisplay ?: stringResource(R.string.from),
                isSet = dateFromApi != null,
                onClear = { dateFromApi = null; dateFromDisplay = null },
                onClick = { showDatePicker.intValue = 1 },
                modifier = Modifier.weight(1f)
            )
            DateChip(
                label = dateToDisplay ?: stringResource(R.string.to),
                isSet = dateToApi != null,
                onClear = { dateToApi = null; dateToDisplay = null },
                onClick = { showDatePicker.intValue = 2 },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Apply Button ──
        Button(
            onClick = {
                onApply(
                    FilterClass(
                        titleTextFrom = dateFromApi,
                        titleTextTo = dateToApi,
                        displayDateFrom = dateFromDisplay,
                        displayDateTo = dateToDisplay,
                        fromPrice = fromPrice.toIntOrNull(),
                        toPrice = toPrice.toIntOrNull(),
                        categoryIds = selectedCategoryIds,
                        categoryNames = selectedCategoryNames,
                        sort = selectedSort,
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = stringResource(R.string.txt_apply_filter),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen24Dp))
    }
}


@Composable
private fun FilterSectionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
    }
}

@Composable
private fun FilterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = {
            Text(placeholder, fontSize = 14.sp)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
private fun DateChip(
    label: String,
    isSet: Boolean,
    onClear: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSet) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Icon(
                imageVector = Icons.Filled.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (isSet) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontFamily = robotoFontFamily,
                fontSize = 13.sp,
                fontWeight = if (isSet) FontWeight.Medium else FontWeight.Normal,
                maxLines = 1
            )
        }
        if (isSet) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.filter_clear),
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onClear() },
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
