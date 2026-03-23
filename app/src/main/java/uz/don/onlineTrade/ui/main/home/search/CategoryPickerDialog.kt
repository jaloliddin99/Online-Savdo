package uz.don.onlineTrade.ui.main.home.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import uz.don.onlineTrade.BuildConfig
import uz.don.onlineTrade.R
import uz.don.onlineTrade.data.remote.models.category.Category
import uz.don.onlineTrade.data.remote.models.category.CategoryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerDialog(
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
        if (ids.contains(item.id)) ids.remove(item.id) else ids.add(item.id)
        selectedIds.value = ids
    }

    fun toggleParent(item: CategoryItem) {
        val ids = selectedIds.value.toMutableSet()
        val allChildIds = collectAllChildIds(item)
        if (allChildIds.all { it in ids }) ids.removeAll(allChildIds) else ids.addAll(allChildIds)
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

    val sheetBg = MaterialTheme.colorScheme.surfaceContainerLow

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = dialogSheetState,
        containerColor = sheetBg,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.select_category_without),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                val count = selectedIds.value.size
                if (count > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$count selected",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

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
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories?.let { cats ->
                        items(cats, key = { it.id }) { item ->
                            CheckboxCategoryCard(
                                item = item,
                                selectedIds = selectedIds.value,
                                onToggleLeaf = { toggleCategory(it) },
                                onToggleParent = { toggleParent(it) }
                            )
                        }
                    }
                }
            }

            // Apply button
            Button(
                onClick = { onApply(collectSelectedItems(categories ?: emptyList())) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = stringResource(R.string.txt_apply_filter),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun collectAllChildIds(item: CategoryItem): Set<Int> {
    if (item.children.isEmpty()) return setOf(item.id)
    return item.children.flatMap { collectAllChildIds(it) }.toSet()
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

    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300),
        label = "chevron"
    )

    Column(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (hasChildren) isExpanded = !isExpanded
                    else onToggleLeaf(item)
                }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TriStateCheckbox(
                state = when {
                    allSelected -> ToggleableState.On
                    someSelected -> ToggleableState.Indeterminate
                    else -> ToggleableState.Off
                },
                onClick = {
                    if (hasChildren) onToggleParent(item) else onToggleLeaf(item)
                }
            )

            if (item.image != null) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        modifier = Modifier.size(24.dp),
                        model = "${BuildConfig.BASE_URL}categories/image/${item.image}",
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            if (hasChildren) {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotationAngle)
                )
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = tween(300)),
            exit = shrinkVertically(animationSpec = tween(300))
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
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
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
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (hasChildren) {
                TriStateCheckbox(
                    state = when {
                        allSelected -> ToggleableState.On
                        someSelected -> ToggleableState.Indeterminate
                        else -> ToggleableState.Off
                    },
                    onClick = { onToggleParent(item) }
                )
            } else {
                Checkbox(
                    checked = item.id in selectedIds,
                    onCheckedChange = { onToggleLeaf(item) }
                )
            }

            if (item.image != null) {
                AsyncImage(
                    modifier = Modifier.size(18.dp),
                    model = "${BuildConfig.BASE_URL}categories/image/${item.image}",
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            if (hasChildren) {
                val angle by animateFloatAsState(
                    targetValue = if (isExpanded) 180f else 0f,
                    label = "child_chevron"
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(angle)
                )
            }
        }

        AnimatedVisibility(visible = isExpanded) {
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
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 48.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}
