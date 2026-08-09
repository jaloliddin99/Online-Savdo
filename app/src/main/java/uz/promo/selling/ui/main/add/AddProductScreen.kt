package uz.promo.selling.ui.main.add

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.category.CategoryItem
import uz.promo.selling.data.remote.models.post.PostParamDTO
import uz.promo.selling.data.remote.models.post.PostValueDTO
import uz.promo.selling.data.remote.models.post.toPostDto
import uz.promo.selling.ui.TopAppBar
import uz.promo.selling.ui.main.add.dynamic.DynamicViewData
import uz.promo.selling.ui.map.MapScreenData
import uz.promo.selling.ui.theme.spacing
import uz.promo.selling.utils.ComposeFileProvider
import uz.promo.selling.utils.FreeLoading
import uz.promo.selling.utils.localizedError
import uz.promo.selling.utils.runTimePermission.RunTimePermission


@Composable
fun AddProductRoute(
    navigateToCategories: () -> Unit,
    modifier: Modifier = Modifier,
    item: CategoryItem? = null,
    map: MapScreenData? = null,
    addProductViewModel: AddProductScreenViewModel = hiltViewModel(),
    goToDetailsPage: () -> Unit,
    goToMapScreen: () -> Unit,
    onCreatingChange: (Boolean) -> Unit = {}
) {
    // null = entry chooser, "AI" = AI flow (Phase 2/3), "MANUAL" = current wizard.
    var mode by rememberSaveable { mutableStateOf<String?>(null) }

    // Tell the host to hide the floating tab bar while a create flow is active,
    // and restore it when this screen leaves composition.
    LaunchedEffect(mode) { onCreatingChange(mode != null) }
    DisposableEffect(Unit) {
        onDispose { onCreatingChange(false) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = stringResource(R.string.chat_page),
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )
        when (mode) {
            null -> CreatePostChooserView(
                onCreateWithAi = { mode = "AI" },
                onCreateManually = { mode = "MANUAL" },
                modifier = modifier.weight(1f)
            )

            "AI" -> AiCreateFlow(
                viewModel = addProductViewModel,
                item = item,
                map = map,
                onLocationClick = goToMapScreen,
                onChangeCategory = navigateToCategories,
                onBack = { mode = null },
                onPublished = goToDetailsPage,
                modifier = modifier.weight(1f)
            )

            else -> AddProductScreen(
                modifier = modifier.weight(1f),
                navigateToCategories,
                item,
                map,
                submitProduct = { titleProduct,
                                  descriptionProduct,
                                  categoryId,
                                  images,
                                  mapData,
                                  postParams ->
                    addProductViewModel.postNewProduct(
                        titleProduct = titleProduct,
                        descriptionProduct = descriptionProduct,
                        categoryId = categoryId,
                        images = images,
                        mapData = mapData,
                        postParams = postParams
                    )
                },
                addProductViewModel,
                goToDetailsPage,
                goToMapScreen
            )
        }
    }
}


@Composable
fun AddProductScreen(
    modifier: Modifier = Modifier,
    navigateToCategories: () -> Unit,
    item: CategoryItem? = null,
    map: MapScreenData? = null,
    submitProduct: (
        titleProduct: String,
        descriptionProduct: String,
        categoryId: Int,
        images: List<ImageUrl>,
        mapData: MapScreenData,
        postParams: List<PostParamDTO>
    ) -> Unit,
    viewModel: AddProductScreenViewModel,
    goToDetailsPage: () -> Unit,
    goToMapScreen: () -> Unit
) {

    val context = LocalContext.current
    val currentStep = viewModel.currentStep
    var swipeDirection by remember { mutableIntStateOf(1) }
    var showPreview by remember { mutableStateOf(false) }

    val state = viewModel.state.value
    val isLoading = state.isLoading
    if (state.showSuccessDialog) {
        NotifyDialog(onDismiss = {
            viewModel.updateShowSuccessDialog(false)
            state.postNewProduct?.let {
                goToDetailsPage.invoke()
            }
        })
    }

    if (item != null) {
        LaunchedEffect(key1 = Unit) {
            viewModel.getCategoryDerails(item.id)
        }
    }

    val productTitleState by remember {
        mutableStateOf(viewModel.titleValue)
    }

    val productDescriptionState by remember {
        mutableStateOf(viewModel.descriptionVM)
    }

    var galleryImageUri by remember {
        mutableStateOf(viewModel.imageList)
    }

    // Field values live in the VM — this composable is disposed while the map /
    // category screens are on top, so local state would lose everything.
    var dynamicViewData by viewModel::dynamicViewData

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { it ->
            galleryImageUri = galleryImageUri + it.map { it.toImageUrl(isFromCamera = false, it) }
            viewModel.setImageList(galleryImageUri)
        }

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { _ ->
            galleryImageUri =
                galleryImageUri + listOf(ImageUrl(true, capturedImageUri, capturedImageUri))
            viewModel.setImageList(galleryImageUri)
        }
    )

    val focusRequester = remember { FocusRequester() }
    val descriptionFocusRequester = remember { FocusRequester() }
    val paddingValues = WindowInsets.systemBars.asPaddingValues()

    // Initialize dynamic view data from category details. Rebuild only when
    // empty or when the loaded category actually changed — re-entering this
    // screen (e.g. back from the map) must keep the user's values.
    if (state.categoryDetail != null) {
        val params = state.categoryDetail.parameters
        if (dynamicViewData.isEmpty() || viewModel.dynamicViewDataCategoryId != state.categoryDetail.id) {
            viewModel.dynamicViewDataCategoryId = state.categoryDetail.id
            dynamicViewData = params.associateBy(
                keySelector = { it.code },
                valueTransform = {
                    DynamicViewData(
                        isRequired = it.validation.is_required,
                        isValid = false,
                        code = it.code,
                        label_ru = it.label_ru,
                        label_uz = it.label_uz,
                        type = it.type,
                        post_value = listOf(
                            PostValueDTO(
                                label_uz = "",
                                label_ru = "",
                                key = ""
                            )
                        ),
                        unit = null
                    )
                }
            )
        }
    }

    // Merge AI-suggested params into the dynamic fields (best-effort, matched by
    // code). Codes that don't belong to the chosen category are simply ignored.
    LaunchedEffect(state.aiDraft) {
        val params = state.aiDraft?.postParams
        if (params.isNullOrEmpty() || dynamicViewData.isEmpty()) return@LaunchedEffect
        val merged = dynamicViewData.toMutableMap()
        params.forEach { p ->
            merged[p.code]?.let { existing ->
                merged[p.code] = existing.copy(
                    post_value = p.post_value,
                    unit = p.param_unit,
                    isValid = true
                )
            }
        }
        dynamicViewData = merged
    }

    // Per-step validation
    val isStep1Valid = item != null && item.id != -1 && map != null
    val isStep2Valid = productTitleState.isValid && productDescriptionState.isValid
    val isStep3Valid = galleryImageUri.isNotEmpty()
            && galleryImageUri.size < 10
            && dynamicDataCorrect(dynamicViewData)
    val isSubmitEnabled = isStep1Valid && isStep2Valid && isStep3Valid

    val isNextEnabled = when (currentStep) {
        1 -> isStep1Valid
        2 -> isStep2Valid
        3 -> isStep3Valid
        else -> false
    }

    val onSubmit = {
        submitProduct(
            productTitleState.text,
            productDescriptionState.text,
            item!!.id,
            galleryImageUri,
            map!!,
            getOnlyValidOptions(dynamicViewData).map { it.toPostDto() }
        )
    }

    fun navigateNext() {
        swipeDirection = 1
        viewModel.goToNextStep()
    }

    fun navigatePrevious() {
        swipeDirection = -1
        viewModel.goToPreviousStep()
    }

    // Back handler: go to previous step instead of exiting screen
    BackHandler(enabled = currentStep > 1) {
        navigatePrevious()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = MaterialTheme.spacing.dimen16Dp,
                end = MaterialTheme.spacing.dimen16Dp,
                bottom = paddingValues.calculateBottomPadding()
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Progress bar + step label
            WizardProgressBar(currentStep)
            StepLabel(currentStep)

            // Animated step content
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    val direction = if (swipeDirection >= 0) 1 else -1
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth * direction },
                        animationSpec = tween(300)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth * direction },
                        animationSpec = tween(300)
                    )
                },
                modifier = Modifier.weight(1f),
                label = "wizard_step_transition"
            ) { step ->
                when (step) {
                    1 -> StepCategoryAndLocation(
                        item = item,
                        map = map,
                        onCategoryClick = {
                            viewModel.setImageList(galleryImageUri)
                            navigateToCategories()
                        },
                        onMapClick = {
                            viewModel.setImageList(galleryImageUri)
                            goToMapScreen()
                        }
                    )

                    2 -> StepBasicInfo(
                        titleState = productTitleState,
                        descriptionState = productDescriptionState,
                        focusRequester = focusRequester,
                        descriptionFocusRequester = descriptionFocusRequester
                    )

                    3 -> StepPhotosAndDetails(
                        imagesList = galleryImageUri,
                        onAddImageClicked = {
                            viewModel.updateSHowCameraOrGallery(true)
                        },
                        onCancelImage = {
                            val list = galleryImageUri.toMutableList()
                            list.remove(it)
                            galleryImageUri = list
                            viewModel.setImageList(list)
                        },
                        categoryParams = state.categoryDetail?.parameters,
                        dynamicViewData = dynamicViewData,
                        onDynamicViewDataChanged = { dynamicViewData = it }
                    )
                }
            }

            // Bottom navigation bar
            WizardBottomBar(
                currentStep = currentStep,
                isNextEnabled = isNextEnabled,
                isSubmitEnabled = isSubmitEnabled,
                onBack = { navigatePrevious() },
                onNext = { navigateNext() },
                onPreview = { showPreview = true },
                onSubmit = onSubmit
            )
        }

        FreeLoading(isFeedLoading = isLoading)
    }

    // Preview bottom sheet
    if (showPreview) {
        ProductPreviewBottomSheet(
            onDismiss = { showPreview = false },
            categoryName = item?.title,
            address = map?.addressName,
            title = productTitleState.text,
            description = productDescriptionState.text,
            images = galleryImageUri,
            dynamicViewData = dynamicViewData
        )
    }

    // Camera/Gallery dialog
    if (state.showCameraOrGalleryDialog) {
        DialogCameraOrGallery(onDismiss = {
            viewModel.updateSHowCameraOrGallery(false)
        }, onCameraSelected = {
            RunTimePermission().permissionListForCamera(
                cameraPermission = {
                    if (it) {
                        val uri = ComposeFileProvider.getImageUri(context)
                        capturedImageUri = uri
                        cameraLauncher.launch(uri)
                    }
                }, context
            )
            viewModel.updateSHowCameraOrGallery(false)
        }, onGallerySelected = {
            RunTimePermission().permissionForGallery(
                galleryPermission = {
                    if (it) {
                        galleryLauncher.launch("image/*")
                    }
                }, context
            )
            viewModel.updateSHowCameraOrGallery(false)
        })
    }

    if (state.error.isNotEmpty()) {
        Toast.makeText(context, localizedError(context, state.error), Toast.LENGTH_SHORT).show()
    }
}

private fun dynamicDataCorrect(map: Map<String, DynamicViewData>): Boolean {
    map.forEach { (s, dynamicViewData) ->
        if (dynamicViewData.isRequired) {
            if (!dynamicViewData.isValid) {
                Log.d(
                    "TAG",
                    "dynamicDataCorrecdawdawdkjjanwd, $s, ${dynamicViewData.label_uz}, ${dynamicViewData.post_value}"
                )
                return false
            }
        }
    }
    return true
}

private fun getOnlyValidOptions(map: Map<String, DynamicViewData>): List<DynamicViewData> {
    val newList = map.values.toList().filter {
        it.isValid
    }
    return newList
}
