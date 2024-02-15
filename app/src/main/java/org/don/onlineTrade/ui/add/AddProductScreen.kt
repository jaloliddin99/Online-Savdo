package org.don.onlineTrade.ui.add

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.category.CategoryItem
import org.don.onlineTrade.data.remote.models.post.PostParamDTO
import org.don.onlineTrade.data.remote.models.post.PostValueDTO
import org.don.onlineTrade.data.remote.models.post.toPostDto
import org.don.onlineTrade.ui.add.dynamic.DynamicView
import org.don.onlineTrade.ui.add.dynamic.DynamicViewData
import org.don.onlineTrade.ui.add.dynamic.TitleWrapper
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

    Log.d("TAG", "AddProductScreendwadawdawd $item")

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
    val descriptionFocusRequester = remember {
        FocusRequester()
    }
    val paddingValues = WindowInsets.systemBars.asPaddingValues()

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
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start,
        ) {

            TitleWrapper(titleRes = R.string.enter_title) {
                TextFieldForProduct(
                    productState = productTitleState,
                    onImeAction = {
                        focusRequester.requestFocus()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            TitleWrapper(titleRes = R.string.add_description) {
                TextFieldForProduct(
                    productState = productDescriptionState,
                    onImeAction = {
                        descriptionFocusRequester.requestFocus()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .height(200.dp),
                    title = R.string.please_enter_description
                )
            }

            TitleWrapper(titleRes = R.string.select_category) {
                TextFieldUnEditable(
                    productTitle = item?.title,
                    modifier = Modifier.fillMaxWidth(),
                    title = R.string.please_select_category,
                    isFocusedOrClicked = {
                        viewModel.setImageList(galleryImageUri)
                        navigateToCategories()
                    }
                )
            }
            TitleWrapper(titleRes = R.string.enter_your_address) {
                TextFieldUnEditable(
                    productTitle = map?.addressName,
                    modifier = Modifier.fillMaxWidth(),
                    title = R.string.enter_your_address,
                    isFocusedOrClicked = {
                        viewModel.setImageList(galleryImageUri)
                        goToMapScreen()
                    }
                )
            }

            TitleWrapper(titleRes = R.string.select_image_for_your_product) {
                ShowSelectedImages(imagesList = galleryImageUri, onAddButtonClicked = {
                    viewModel.updateSHowCameraOrGallery(true)
                }, cancelClicked = {
                    val list = galleryImageUri.toMutableList()
                    list.remove(it)
                    galleryImageUri = list
                })
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))

            val isEnabled =
                productTitleState.isValid
                        && productDescriptionState.isValid
                        && item?.id != -1
                        && (galleryImageUri.isNotEmpty() && galleryImageUri.size < 10)
                        && map != null
                        && dynamicDataCorrect(dynamicViewData)


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


            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen16Dp))

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
                DynamicView(params, dynamicViewData) {
                    dynamicViewData = it
                }
            }


            Button(
                onClick = onSubmit,
                enabled = isEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 3.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.upload),
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen24Dp))
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

        }
        FreeLoading(isFeedLoading = isLoading)
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