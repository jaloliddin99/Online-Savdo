package org.don.onlineTrade.ui.add

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.category.CategoryItem
import org.don.onlineTrade.data.remote.models.currencies.ModelCurrencyListsItem
import org.don.onlineTrade.ui.add.dynamic.DynamicView
import org.don.onlineTrade.ui.home.getCurrencyList
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.ComposeFileProvider
import org.don.onlineTrade.utils.FreeLoading
import org.don.onlineTrade.utils.runTimePermission.RunTimePermission


@Composable
fun AddProductRoute(
    navigateToCategories: () -> Unit,
    navigateToSelectRegions: () -> Unit,
    modifier: Modifier = Modifier,
    item: CategoryItem? = null,
    regName: String? = null,
    disName: String? = null,
    regId: Int? = null,
    disId: Int? = null,
    lat: String? = null,
    lon: String? = null,
    addProductViewModel: AddProductScreenViewModel = hiltViewModel(),
    goToDetailsPage: (Int) -> Unit

) {


    AddProductScreen(
        modifier = modifier,
        navigateToCategories,
        navigateToSelectRegions,
        item,
        submitProduct = { titleProduct,
                          descriptionProduct,
                          priceText, currencyId,
                          region, district,
                          categoryId,
                          images,
                          selectedOption->
            addProductViewModel.postNewProduct(
                titleProduct = titleProduct,
                descriptionProduct = descriptionProduct,
                priceText = priceText,
                currencyId = currencyId,
                region = region,
                districtId = district,
                categoryId = categoryId,
                lat = lat,
                lon = lon,
                images = images,
                selectedOption = selectedOption
            )

        },
        regName,
        disName,
        regId,
        disId,
        addProductViewModel,
        goToDetailsPage
    )
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddProductScreen(
    modifier: Modifier = Modifier,
    navigateToCategories: () -> Unit,
    navigateToSelectRegions: () -> Unit,
    item: CategoryItem? = null,
    submitProduct: (
        titleProduct: String,
        descriptionProduct: String,
        priceText: String,
        currencyId: Int,
        region: Int,
        district: Int,
        categoryId: Int,
        images: List<ImageUrl>,
        selectedOption: Int
    ) -> Unit,
    regName: String? = null,
    disName: String? = null,
    regId: Int? = null,
    disId: Int? = null,
    viewModel: AddProductScreenViewModel,
    goToDetailsPage: (Int) -> Unit
) {

    val context = LocalContext.current

    val state = viewModel.state.value
    val isLoading = state.isLoading
    if (state.error.isNotEmpty()) {
        Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
    }
    if (state.showSuccessDialog) {
        NotifyDialog(onDismiss = {
            viewModel.updateShowSuccessDialog(false)
            state.postNewProduct?.data?.id?.let(goToDetailsPage)
        })
    }

    if (item != null) {
        viewModel.categoryValue(item)
        LaunchedEffect(key1 =Unit){
            Log.d("TAG", "AddProductScreendawdakwjdawd1 ${state.categoryDetail}")

            viewModel.getCategoryDerails(item.id)
        }
    }


    var showGalleryOrCameraDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val productTitleState by rememberSaveable(stateSaver = ProductTitleStateSaver) {
        mutableStateOf(viewModel.titleValue)
    }

    val productDescriptionState by rememberSaveable(stateSaver = ProductDescriptionStateSaver) {
        mutableStateOf(viewModel.descriptionVM)
    }


    val productPriceState by rememberSaveable(stateSaver = ProductPriceStateSaver) {
        mutableStateOf(viewModel.priceVM)
    }

    var currencyItem by remember {
        mutableStateOf(ModelCurrencyListsItem())
    }

    var galleryImageUri by remember {
        mutableStateOf(viewModel.imageList)
    }

    var selectedOption by remember {
        mutableIntStateOf(1)
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
        onResult = { success ->
            galleryImageUri =
                galleryImageUri + listOf(ImageUrl(true, capturedImageUri, capturedImageUri))
        }
    )

    val focusRequester = remember { FocusRequester() }
    val descriptionFocusRequester = remember {
        FocusRequester()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    horizontal = MaterialTheme.spacing.dimen16Dp
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start,
        ) {
            ProductTitle(title = R.string.enter_title)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen2Dp))
            TextFieldForProduct(
                productState = productTitleState, onImeAction = {
                    focusRequester.requestFocus()
                }, modifier = Modifier.fillMaxWidth()
            )

            DividerTextAndSpace(R.string.add_description)
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
            DividerTextAndSpace(R.string.select_category)
            TextFieldUnEditable(productTitle = viewModel.categoryValue.title,
                modifier = Modifier.fillMaxWidth(),
                title = R.string.please_select_category,
                isFocusedOrClicked = {
                    navigateToCategories()
                })
            DividerTextAndSpace(R.string.select_region)


            TextFieldUnEditable(productTitle = if (regId != -1) "$regName, $disName" else "",
                modifier = Modifier.fillMaxWidth(),
                title = R.string.please_select_region,
                isFocusedOrClicked = {
                    viewModel.setTitle(productTitleState)
                    viewModel.setDescription(productDescriptionState)
                    viewModel.setPrice(productPriceState as ProductPriceState)
                    viewModel.setImageList(galleryImageUri)
                    navigateToSelectRegions()
                })


            DividerTextAndSpace(R.string.select_image_for_your_product)

            ShowSelectedImages(imagesList = galleryImageUri, onAddButtonClicked = {
                showGalleryOrCameraDialog = true
            }, cancelClicked = {
                val list = galleryImageUri.toMutableList()
                list.remove(it)
                galleryImageUri = list
            })

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))

            val categoryIsAdded = viewModel.categoryValue.id != -1
            val regionIsAdded = disId != null

            val isEnabled =
                productTitleState.isValid
                        && productDescriptionState.isValid
                        && categoryIsAdded
                        && regionIsAdded
                        && productPriceState.isValid
                        && currencyItem.id != -1
                        && (galleryImageUri.isNotEmpty() && galleryImageUri.size < 5)

            val onSubmit = {
                if (!productTitleState.isValid) {
                    productTitleState.enableShowErrors()
                }
                if (!productDescriptionState.isValid) {
                    productDescriptionState.enableShowErrors()
                }
                if (!productPriceState.isValid) {
                    productPriceState.enableShowErrors()
                }
                if (currencyItem.id == -1){
                    Toast.makeText(context, context.getString(R.string.select_currency), Toast.LENGTH_SHORT).show()
                }

                submitProduct(
                    productTitleState.text,
                    productDescriptionState.text,
                    productPriceState.text,
                    currencyItem.id,
                    regId!!,
                    disId!!,
                    viewModel.categoryValue.id,
                    galleryImageUri,
                    selectedOption
                )
            }


            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen16Dp))

            if (state.categoryDetail != null){
                DynamicView(state.categoryDetail.parameters)
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
            if (showGalleryOrCameraDialog) {
                DialogCameraOrGallery(onDismiss = {
                    showGalleryOrCameraDialog = false
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
                    showGalleryOrCameraDialog = false
                }, onGallerySelected = {
                    RunTimePermission().permissionForGallery(
                        galleryPermission = {
                            if (it) {
                                galleryLauncher.launch("image/*")
                            }
                        }, context
                    )
                    showGalleryOrCameraDialog = false
                })
            }

        }
        FreeLoading(isFeedLoading = isLoading)
    }

}


@Composable
fun DividerTextAndSpace(@StringRes title: Int) {
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
    ProductTitle(title = title)
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen2Dp))

}
