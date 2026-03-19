package org.don.onlineTrade.ui.home.search

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.don.onlineTrade.BuildConfig
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.category.CategoryParent
import org.don.onlineTrade.ui.home.search.filter.FilterClass
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.convertLongToDateString
import org.don.onlineTrade.utils.convertLongToDisplayDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheetContent(
    currentRadiusKm: Int,
    categories: List<CategoryParent>,
    initialFilter: FilterClass,
    onApply: (FilterClass) -> Unit,
    onReset: () -> Unit,
    onLocationClick: () -> Unit,
) {
    // API format (yyyy-MM-dd) for sending to backend
    var dateFromApi by remember { mutableStateOf(initialFilter.titleTextFrom) }
    var dateToApi by remember { mutableStateOf(initialFilter.titleTextTo) }
    // Display format (Mar 19, 2026) for showing in UI
    var dateFromDisplay by remember { mutableStateOf(initialFilter.displayDateFrom) }
    var dateToDisplay by remember { mutableStateOf(initialFilter.displayDateTo) }

    var fromPrice by remember { mutableStateOf(initialFilter.fromPrice?.toString() ?: "") }
    var toPrice by remember { mutableStateOf(initialFilter.toPrice?.toString() ?: "") }
    var selectedCategoryIds by remember { mutableStateOf(initialFilter.categoryIds) }
    var selectedCategoryNames by remember { mutableStateOf(initialFilter.categoryNames) }

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
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker.intValue = 0 }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val hasAnyFilter = dateFromApi != null || dateToApi != null ||
            fromPrice.isNotBlank() || toPrice.isNotBlank() || selectedCategoryIds.isNotEmpty()

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
                text = "Filters",
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
                    onReset()
                }) {
                    Text("Reset all", color = MaterialTheme.colorScheme.error)
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
                        text = if (currentRadiusKm > 0) "${currentRadiusKm} km radius"
                        else "No location set",
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Tap to change location & radius",
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

        if (categories.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val catId = category.id.toLong()
                    val isSelected = catId in selectedCategoryIds
                    SelectableCategoryChip(
                        title = category.title,
                        imageUrl = category.image?.let { "${BuildConfig.BASE_URL}categories/image/$it" },
                        isSelected = isSelected,
                        onClick = {
                            if (isSelected) {
                                selectedCategoryIds = selectedCategoryIds - catId
                                selectedCategoryNames = selectedCategoryNames - category.title
                            } else {
                                selectedCategoryIds = selectedCategoryIds + catId
                                selectedCategoryNames = selectedCategoryNames + category.title
                            }
                        }
                    )
                }
            }
        } else {
            Text(
                text = "Loading categories...",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
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

        // ── Section 4: Date Range ──
        FilterSectionHeader(
            icon = Icons.Filled.CalendarMonth,
            title = "Date Range"
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
private fun SelectableCategoryChip(
    title: String,
    imageUrl: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .clickable { onClick() }
            .padding(start = 4.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(6.dp))
        } else {
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = title,
            fontFamily = robotoFontFamily,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 13.sp,
            color = contentColor
        )
        if (isSelected) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = contentColor
            )
        }
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
                contentDescription = "Clear",
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onClear() },
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
