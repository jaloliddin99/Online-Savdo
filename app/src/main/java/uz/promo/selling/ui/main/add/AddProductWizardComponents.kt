package uz.promo.selling.ui.main.add

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.category.CategoryItem
import uz.promo.selling.data.remote.models.leak.Parameter
import uz.promo.selling.ui.auth.TextFieldState
import uz.promo.selling.ui.detailsPage.CircularPagerIndicator
import uz.promo.selling.ui.detailsPage.DescriptionItems
import uz.promo.selling.ui.main.add.dynamic.DynamicView
import uz.promo.selling.ui.main.add.dynamic.DynamicViewData
import uz.promo.selling.ui.main.add.dynamic.TitleWrapper
import uz.promo.selling.ui.map.MapScreenData
import uz.promo.selling.ui.theme.spacing

private const val TOTAL_STEPS = 3

@Composable
fun WizardProgressBar(currentStep: Int) {
    val progress by animateFloatAsState(
        targetValue = currentStep.toFloat() / TOTAL_STEPS,
        animationSpec = tween(durationMillis = 500),
        label = "wizard_progress"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$currentStep / $TOTAL_STEPS",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun StepLabel(currentStep: Int) {
    val stepName = when (currentStep) {
        1 -> stringResource(R.string.step_category_location)
        2 -> stringResource(R.string.step_basic_info)
        3 -> stringResource(R.string.step_photos_details)
        else -> ""
    }
    Text(
        text = stepName,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(vertical = MaterialTheme.spacing.dimen12Dp)
    )
}

@Composable
fun StepCategoryAndLocation(
    item: CategoryItem?,
    map: MapScreenData?,
    onCategoryClick: () -> Unit,
    onMapClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TitleWrapper(titleRes = R.string.select_category) {
            TextFieldUnEditable(
                productTitle = item?.title,
                modifier = Modifier.fillMaxWidth(),
                title = R.string.please_select_category,
                isFocusedOrClicked = onCategoryClick
            )
        }
        TitleWrapper(titleRes = R.string.enter_your_address) {
            TextFieldUnEditable(
                productTitle = map?.addressName,
                modifier = Modifier.fillMaxWidth(),
                title = R.string.enter_your_address,
                isFocusedOrClicked = onMapClick
            )
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen16Dp))
    }
}

@Composable
fun StepBasicInfo(
    titleState: TextFieldState,
    descriptionState: TextFieldState,
    focusRequester: FocusRequester,
    descriptionFocusRequester: FocusRequester
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TitleWrapper(titleRes = R.string.enter_title) {
            TextFieldForProduct(
                productState = titleState,
                onImeAction = { focusRequester.requestFocus() },
                modifier = Modifier.fillMaxWidth()
            )
        }
        TitleWrapper(titleRes = R.string.add_description) {
            TextFieldForProduct(
                productState = descriptionState,
                onImeAction = { descriptionFocusRequester.requestFocus() },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .height(200.dp),
                title = R.string.please_enter_description
            )
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen16Dp))
    }
}

@Composable
fun StepPhotosAndDetails(
    imagesList: List<ImageUrl>,
    onAddImageClicked: () -> Unit,
    onCancelImage: (ImageUrl) -> Unit,
    categoryParams: List<Parameter>?,
    dynamicViewData: Map<String, DynamicViewData>,
    onDynamicViewDataChanged: (Map<String, DynamicViewData>) -> Unit,
    onAutoFill: () -> Unit = {},
    isAiLoading: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // AI: fill title/description + details from the photos.
        OutlinedButton(
            onClick = onAutoFill,
            enabled = imagesList.isNotEmpty() && !isAiLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = if (isAiLoading) "Analyzing photos…" else "✨ Auto-fill from photos")
        }
        TitleWrapper(titleRes = R.string.select_image_for_your_product) {
            ShowSelectedImages(
                imagesList = imagesList,
                onAddButtonClicked = onAddImageClicked,
                cancelClicked = onCancelImage
            )
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))

        if (categoryParams != null) {
            DynamicView(categoryParams, dynamicViewData) {
                onDynamicViewDataChanged(it)
            }
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen16Dp))
    }
}

// ── Preview Bottom Sheet ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProductPreviewBottomSheet(
    onDismiss: () -> Unit,
    categoryName: String?,
    address: String?,
    title: String,
    description: String,
    images: List<ImageUrl>,
    dynamicViewData: Map<String, DynamicViewData>
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = MaterialTheme.spacing.dimen24Dp)
        ) {
            // Image pager
            if (images.isNotEmpty()) {
                val pagerState = rememberPagerState(pageCount = { images.size })
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        Image(
                            painter = rememberAsyncImagePainter(model = images[page].uri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    CircularPagerIndicator(
                        itemCount = images.size,
                        currentPage = pagerState.currentPage
                    )
                }
            }

            // Product info section (mirrors ItemDescription)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.spacing.dimen16Dp,
                        vertical = MaterialTheme.spacing.dimen12Dp
                    )
            ) {
                // Title
                TextNormal16(title = title.ifEmpty { "—" })

                // Price from dynamic data
                val priceEntry = dynamicViewData.values.find { it.code == "price" && it.isValid }
                if (priceEntry != null) {
                    val priceValue = priceEntry.post_value.firstOrNull()?.label_uz ?: ""
                    val priceUnit = priceEntry.unit?.label ?: ""
                    TextBold16(title = "$priceValue $priceUnit")
                }

                // Characteristics
                val paramEntries = dynamicViewData.values.filter {
                    it.isValid && it.code != "price" && it.type != "multichoice"
                }
                if (paramEntries.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))
                    TextNormal16(title = stringResource(R.string.characteristics))
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen4Dp))
                    paramEntries.forEach { data ->
                        val valueText = data.post_value.joinToString(", ") { it.label_uz }
                        val unitText = data.unit?.let { " ${it.label}" } ?: ""
                        TextNormal14(title = "● ${data.label_uz} — $valueText$unitText")
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen2Dp))
                    }
                }

                // Multichoice
                val multiEntries = dynamicViewData.values.filter {
                    it.isValid && it.type == "multichoice"
                }
                multiEntries.forEach { data ->
                    TextNormal14(title = "● ${data.label_uz}")
                    data.post_value.forEach { value ->
                        TextNormal12(title = "   — ${value.label_uz}")
                    }
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))
                TextThin(title = description.ifEmpty { "—" })

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.dimen10Dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // Location
                DescriptionItems(
                    desc = address ?: "—",
                    onClicked = {}
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.dimen10Dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // Category
                DescriptionItems(
                    imageVector = Icons.Filled.Category,
                    title = stringResource(R.string.category),
                    desc = categoryName ?: "—",
                    onClicked = {}
                )
            }
        }
    }
}

// ── Bottom Bar ──────────────────────────────────────────────────────────────────

@Composable
fun WizardBottomBar(
    currentStep: Int,
    isNextEnabled: Boolean,
    isSubmitEnabled: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onPreview: () -> Unit,
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.dimen8Dp),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.dimen12Dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (currentStep > 1) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.back))
            }
        }

        if (currentStep < TOTAL_STEPS) {
            // Steps 1 & 2: Next button
            Button(
                onClick = onNext,
                enabled = isNextEnabled,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.next))
            }
        } else {
            // Step 3: Preview + Submit
            OutlinedButton(
                onClick = onPreview,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.preview))
            }
            Button(
                onClick = onSubmit,
                enabled = isSubmitEnabled,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.upload))
            }
        }
    }
}
