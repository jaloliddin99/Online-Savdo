package uz.promo.selling.ui.categoriesList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import uz.promo.selling.BuildConfig
import uz.promo.selling.data.remote.models.category.CategoryItem
import uz.promo.selling.data.remote.models.category.CategoryParent
import uz.promo.selling.data.remote.models.category.ParentCategories
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.ui.theme.spacing

@Composable
fun Categories(
    state: ParentCategories,
    navigateToCategory: (Int) -> Unit,
    modifier: Modifier = Modifier

) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(
                rememberScrollState()
            )
    ) {
        state.forEachIndexed { index, item ->
            Spacer(modifier = modifier.width(MaterialTheme.spacing.dimen8Dp))
            CategoryItem(
                item = item,
                modifier = modifier
                    .width(MaterialTheme.spacing.dimen100Dp)
                    .height(MaterialTheme.spacing.dimen120Dp),
                navigateToCategory = navigateToCategory
            )
            if (index == state.lastIndex) {
                Spacer(modifier = modifier.width(MaterialTheme.spacing.dimen8Dp))
            }
        }
    }
}

@Composable
fun CategoryItem(
    item: CategoryParent,
    modifier: Modifier = Modifier,
    navigateToCategory: (Int) -> Unit
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(MaterialTheme.spacing.dimen12Dp),
        onClick = {
            navigateToCategory(item.id)
        },

        ) {
        Column(
            modifier = modifier
                .padding(
                    start = MaterialTheme.spacing.dimen8Dp,
                    end = MaterialTheme.spacing.dimen8Dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            val url = "${BuildConfig.BASE_URL}categories/image/${item.image}"
            AsyncImage(
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp),
                model = url, contentDescription = null
            )

            Text(
                text = item.title,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CategoryItemInVertical(
    item: CategoryItem,
    onItemSelected: (CategoryItem) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable {
                    if (item.children.isNotEmpty()) {
                        isExpanded = !isExpanded
                    } else {
                        onItemSelected(item)
                    }
                }
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (item.image != null) {
                val url = "${BuildConfig.BASE_URL}categories/image/${item.image}"
                AsyncImage(
                    modifier = Modifier
                        .width(MaterialTheme.spacing.dimen20Dp)
                        .height(MaterialTheme.spacing.dimen20Dp),
                    model = url, contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = item.title,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.weight(1f))
            if (item.children.isNotEmpty()) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

        }
        if (isExpanded) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(0.5.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .height((item.children.size * 56.5).dp)
                    .padding(start = 24.dp)
            ) {
                items(item.children) { childItem ->
                    CategoryItemInVertical(
                        item = childItem,
                        onItemSelected = onItemSelected
                    )
                }
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .height(0.5.dp)
        )
    }

}


