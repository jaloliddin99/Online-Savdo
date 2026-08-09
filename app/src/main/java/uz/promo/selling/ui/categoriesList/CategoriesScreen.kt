package uz.promo.selling.ui.categoriesList

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import uz.promo.selling.BuildConfig
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.category.CategoryItem
import uz.promo.selling.ui.TopAppBar
import uz.promo.selling.ui.main.home.HomeScreenState
import uz.promo.selling.ui.main.home.HomeViewModel
import uz.promo.selling.ui.theme.spacing
import uz.promo.selling.utils.FreeLoading
import uz.promo.selling.utils.localizedError

@Composable
fun CategoriesRoute(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    onBackPressed: (CategoryItem) -> Unit,
    onBackClick: () -> Unit,
) {
    val state = homeViewModel.state.value
    LaunchedEffect(key1 = homeViewModel) {
        homeViewModel.getAllCategories()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = stringResource(R.string.select_category_without),
            navigationIcon = Icons.Filled.ArrowBack,
            onNavigationClick = onBackClick,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )
        CategoriesScreen(
            modifier = modifier.weight(1f),
            state = state,
            onBackPressed = onBackPressed
        )
    }
}

@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    state: HomeScreenState,
    onBackPressed: (CategoryItem) -> Unit
) {
    val isFeedLoading = state.isLoading
    val context = LocalContext.current

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = MaterialTheme.spacing.dimen12Dp,
                vertical = MaterialTheme.spacing.dimen8Dp
            )
        ) {
            val categories = state.categoryList ?: return@LazyColumn
            items(categories, key = { it.id }) { item ->
                CategoryCard(
                    item = item,
                    onItemSelected = onBackPressed
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))
            }
        }

        if (state.error.isNotBlank()) {
            Toast.makeText(context, localizedError(context, state.error), Toast.LENGTH_SHORT).show()
        }

        FreeLoading(isFeedLoading = isFeedLoading)
    }
}

@Composable
private fun CategoryCard(
    item: CategoryItem,
    onItemSelected: (CategoryItem) -> Unit
) {
    val hasChildren = item.children.isNotEmpty()
    var isExpanded by remember { mutableStateOf(false) }

    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300),
        label = "chevron_rotation"
    )

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            // Parent row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (hasChildren) {
                            isExpanded = !isExpanded
                        } else {
                            onItemSelected(item)
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category icon
                if (item.image != null) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        val url = "${BuildConfig.BASE_URL}categories/image/${item.image}"
                        AsyncImage(
                            modifier = Modifier.size(24.dp),
                            model = url,
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
                } else {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Children with animated visibility
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
                        ChildCategoryItem(
                            item = child,
                            onItemSelected = onItemSelected
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
}

@Composable
private fun ChildCategoryItem(
    item: CategoryItem,
    onItemSelected: (CategoryItem) -> Unit
) {
    val hasChildren = item.children.isNotEmpty()
    var isExpanded by remember { mutableStateOf(false) }

    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300),
        label = "child_chevron_rotation"
    )

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (hasChildren) {
                        isExpanded = !isExpanded
                    } else {
                        onItemSelected(item)
                    }
                }
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.image != null) {
                val url = "${BuildConfig.BASE_URL}categories/image/${item.image}"
                AsyncImage(
                    modifier = Modifier.size(20.dp),
                    model = url,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(10.dp))
            }

            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
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
                        .size(20.dp)
                        .rotate(rotationAngle)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Nested children
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = tween(300)),
            exit = shrinkVertically(animationSpec = tween(300))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
            ) {
                item.children.forEachIndexed { index, grandChild ->
                    ChildCategoryItem(
                        item = grandChild,
                        onItemSelected = onItemSelected
                    )
                    if (index < item.children.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}
