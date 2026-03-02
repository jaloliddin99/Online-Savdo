package org.don.onlineTrade.ui.add

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import org.don.onlineTrade.data.remote.models.category.CategoryItem
import org.don.onlineTrade.data.remote.models.post.PostParamDTO
import org.don.onlineTrade.data.remote.models.post.PostValueDTO
import org.don.onlineTrade.data.remote.models.post.toPostDto
import org.don.onlineTrade.ui.add.dynamic.DynamicViewData
import org.don.onlineTrade.ui.map.MapScreenData
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.ComposeFileProvider
import org.don.onlineTrade.utils.FreeLoading
import org.don.onlineTrade.utils.runTimePermission.RunTimePermission


@Composable
fun AddProductRoute(
    navigateToCategories: () -> Unit,
    modifier: Modifier = Modifier,
    item: CategoryItem? = null,
    map: MapScreenData? = null,
    addProductViewModel: AddProductScreenViewModel = hiltViewModel(),
    goToDetailsPage: () -> Unit,
    goToMapScreen: () -> Unit
) {
    AddProductScreen(
        modifier = modifier,
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

    var dynamicViewData by remember {
        mutableStateOf(mapOf<String, DynamicViewData>())
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { it ->
            galleryImageUri = galleryImageUri + it.map { it.toImageUrl(isFromCamera = false, it) }
        }

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { _ ->
            galleryImageUri =
                galleryImageUri + listOf(ImageUrl(true, capturedImageUri, capturedImageUri))
        }
    )

    val focusRequester = remember { FocusRequester() }
    val descriptionFocusRequester = remember { FocusRequester() }
    val paddingValues = WindowInsets.systemBars.asPaddingValues()

    // Initialize dynamic view data from category details
    if (state.categoryDetail != null) {
        val params = state.categoryDetail.parameters
        if (dynamicViewData.isEmpty()) {
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
        Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
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
