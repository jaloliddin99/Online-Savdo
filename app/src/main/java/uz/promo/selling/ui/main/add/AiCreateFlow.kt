package uz.promo.selling.ui.main.add

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.promo.selling.data.remote.models.category.CategoryItem
import uz.promo.selling.data.remote.models.post.PostValueDTO
import uz.promo.selling.data.remote.models.post.toPostDto
import uz.promo.selling.ui.main.add.dynamic.DynamicView
import uz.promo.selling.ui.main.add.dynamic.DynamicViewData
import uz.promo.selling.ui.map.MapScreenData

/**
 * AI-first posting flow: add photos (+ location) → "Generate listing" → magic
 * overlay → editable review (title/description/category-fields pre-filled, user
 * adds price) → publish. Reuses the existing dynamic-field UI and submit path.
 */
@Composable
fun AiCreateFlow(
    viewModel: AddProductScreenViewModel,
    item: CategoryItem?,
    map: MapScreenData?,
    onLocationClick: () -> Unit,
    onChangeCategory: () -> Unit,
    onBack: () -> Unit,
    onPublished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.value
    var images by remember { mutableStateOf(viewModel.imageList) }
    var stage by rememberSaveable { mutableStateOf("INPUT") }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        images = images + uris.map { it.toImageUrl(isFromCamera = false, it) }
        viewModel.setImageList(images)
    }

    when (stage) {
        "REVIEW" -> AiReviewScreen(
            viewModel = viewModel,
            item = item,
            images = images,
            map = map,
            onChangeCategory = onChangeCategory,
            onLocationClick = onLocationClick,
            onBack = { stage = "INPUT" },
            onPublished = onPublished,
            modifier = modifier
        )

        else -> Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Add photos — AI does the rest",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            Box {
                ShowSelectedImages(
                    imagesList = images,
                    onAddButtonClicked = { galleryLauncher.launch("image/*") },
                    cancelClicked = { removed ->
                        images = images.filterNot { it == removed }
                        viewModel.setImageList(images)
                    },
                    enabled = !state.isAiLoading
                )
                if (state.isAiLoading) {
                    AiAnalyzingOverlay(Modifier.matchParentSize())
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onLocationClick,
                enabled = !state.isAiLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(map?.addressName?.takeIf { it.isNotBlank() } ?: "Select location")
            }

            Spacer(Modifier.height(20.dp))

            AiAutoFillButton(
                isLoading = state.isAiLoading,
                enabled = images.isNotEmpty(),
                onClick = {
                    viewModel.generateDraftFromImages(images) { success ->
                        if (success) stage = "REVIEW"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                idleText = "✨ Generate listing",
                loadingText = "Analyzing your photos…"
            )

            if (state.error.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(state.error, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Back")
            }
        }
    }
}

@Composable
private fun AiReviewScreen(
    viewModel: AddProductScreenViewModel,
    item: CategoryItem?,
    images: List<ImageUrl>,
    map: MapScreenData?,
    onChangeCategory: () -> Unit,
    onLocationClick: () -> Unit,
    onBack: () -> Unit,
    onPublished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.value
    val draft = state.aiDraft?.draft
    // A manually-changed category (item) wins over the AI's detected one.
    val categoryId = item?.id ?: draft?.categoryId?.toInt()
    val categoryLabel = item?.title ?: draft?.categoryLabel ?: "—"

    // Leave the flow once the post is created.
    LaunchedEffect(state.showSuccessDialog) {
        if (state.showSuccessDialog) {
            viewModel.updateShowSuccessDialog(false)
            onPublished()
        }
    }

    // Load the chosen category's detail so its fields render.
    LaunchedEffect(categoryId) {
        if (categoryId != null) viewModel.getCategoryDerails(categoryId)
    }

    var dynamicViewData by remember { mutableStateOf(mapOf<String, DynamicViewData>()) }

    // Build the fields from the category schema and pre-fill from the AI params.
    LaunchedEffect(state.categoryDetail, state.aiDraft) {
        val detail = state.categoryDetail ?: return@LaunchedEffect
        val base = detail.parameters.associateBy(
            keySelector = { it.code },
            valueTransform = { param ->
                DynamicViewData(
                    isRequired = param.validation.is_required,
                    isValid = false,
                    code = param.code,
                    label_ru = param.label_ru,
                    label_uz = param.label_uz,
                    type = param.type,
                    post_value = listOf(PostValueDTO(key = "", label_uz = "", label_ru = "")),
                    unit = null
                )
            }
        ).toMutableMap()
        state.aiDraft?.postParams?.forEach { p ->
            base[p.code]?.let { existing ->
                base[p.code] = existing.copy(
                    post_value = p.post_value,
                    unit = p.param_unit,
                    isValid = true
                )
            }
        }
        dynamicViewData = base
    }

    val canPublish = viewModel.titleValue.text.length > 3 &&
            viewModel.descriptionVM.text.length > 10 &&
            images.isNotEmpty() && map != null && categoryId != null &&
            dynamicViewData.values.all { !it.isRequired || it.isValid }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Review & publish",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))

        // Category (AI-selected, changeable)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Category", fontWeight = FontWeight.SemiBold)
                Text(categoryLabel)
            }
            TextButton(onClick = onChangeCategory) { Text("Change") }
        }
        Spacer(Modifier.height(12.dp))

        Text("Title", fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = viewModel.titleValue.text,
            onValueChange = { viewModel.titleValue.text = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        Text("Description", fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = viewModel.descriptionVM.text,
            onValueChange = { viewModel.descriptionVM.text = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
        Spacer(Modifier.height(12.dp))

        OutlinedButton(onClick = onLocationClick, modifier = Modifier.fillMaxWidth()) {
            Text(map?.addressName?.takeIf { it.isNotBlank() } ?: "Select location")
        }
        Spacer(Modifier.height(12.dp))

        // Detail fields incl. price — pre-filled by AI (price stays empty for the user).
        state.categoryDetail?.let { detail ->
            DynamicView(detail.parameters, dynamicViewData) { dynamicViewData = it }
        }
        Spacer(Modifier.height(16.dp))

        AiAutoFillButton(
            isLoading = state.isLoading,
            enabled = canPublish && !state.isLoading,
            onClick = {
                if (categoryId != null && map != null) {
                    viewModel.postNewProduct(
                        titleProduct = viewModel.titleValue.text,
                        descriptionProduct = viewModel.descriptionVM.text,
                        categoryId = categoryId,
                        images = images,
                        mapData = map,
                        postParams = dynamicViewData.values.filter { it.isValid }.map { it.toPostDto() }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            idleText = "Publish",
            loadingText = "Publishing…"
        )

        if (state.error.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(state.error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back to photos")
        }
    }
}
