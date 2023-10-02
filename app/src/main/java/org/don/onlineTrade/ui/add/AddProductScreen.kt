package org.don.onlineTrade.ui.add

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.category.CompactedCategoryItem
import org.don.onlineTrade.data.remote.models.currencies.ModelCurrencyListsItem
import org.don.onlineTrade.data.remote.models.region.RegionDistrictModelItem
import org.don.onlineTrade.ui.home.AddProductScreenState
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading
import org.don.onlineTrade.utils.runTimePermission.RunTimePermission
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects


@Composable
fun AddProductRoute(
    navigateToCategories: () -> Unit,
    navigateToSelectRegions: () -> Unit,
    modifier: Modifier = Modifier,
    item: CompactedCategoryItem? = null,
    regions: RegionDistrictModelItem? = null,
    popBack: () -> Unit
) {

    val addProductViewModel = hiltViewModel<AddProductScreenViewModel>()
    val state = addProductViewModel.state.value

    AddProductScreen(
        modifier = modifier,
        navigateToCategories,
        navigateToSelectRegions,
        item,
        regions,
        state,
        submitProduct = { titleProduct: String, descriptionProduct: String, priceText: String, currencyId: Int, region: Int, categoryId: Int, images: List<ImageUrl> ->
            addProductViewModel.postNewProduct(
                titleProduct = titleProduct,
                descriptionProduct = descriptionProduct,
                priceText = priceText,
                currencyId = currencyId,
                region = region,
                categoryId = categoryId,
                images = images
            )
        },
        popBack
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddProductScreen(
    modifier: Modifier,
    navigateToCategories: () -> Unit,
    navigateToSelectRegions: () -> Unit,
    item: CompactedCategoryItem? = null,
    region: RegionDistrictModelItem? = null,
    state: AddProductScreenState,
    submitProduct: (
        titleProduct: String,
        descriptionProduct: String,
        priceText: String,
        currencyId: Int,
        region: Int,
        categoryId: Int,
        images: List<ImageUrl>,
    ) -> Unit,
    popBack: () -> Unit
) {


    var showGalleryOrCameraDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val productTitleState by rememberSaveable(stateSaver = ProductTitleStateSaver) {
        mutableStateOf(ProductTitleState())
    }

    val productDescriptionState by rememberSaveable(stateSaver = ProductDescriptionStateSaver) {
        mutableStateOf(ProductDescriptionState())
    }

    val productPriceState by rememberSaveable(stateSaver = ProductPriceStateSaver) {
        mutableStateOf(ProductPriceState())
    }

    var currencyItem by remember {
        mutableStateOf(ModelCurrencyListsItem())
    }

    val isFeedLoading = state.isLoading
    val context = LocalContext.current

    if (state.error.isNotBlank()) {
        Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
    }

    var images by remember {
        mutableStateOf(listOf<ImageUrl>())
    }

    fun clearItems() {
        images = listOf()
        productTitleState.text = ""
        productDescriptionState.text = ""
        productPriceState.text = ""
        popBack()
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            images = it.map { it.toImageUrl(isFromCamera = false) }
        }


    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context), "org.don.onlineTrade.provider", file
    )

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        capturedImageUri = uri
    }

    val focusRequester = remember { FocusRequester() }
    val descriptionFocusRequester = remember {
        FocusRequester()
    }
    FreeLoading(isFeedLoading)

    val keyboardController = LocalSoftwareKeyboardController.current

    if (state.postNewProduct != null) {
        clearItems()
    }

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
        TextFieldUnEditable(productTitle = item?.title,
            modifier = Modifier.fillMaxWidth(),
            title = R.string.please_select_category,
            isFocusedOrClicked = {
                navigateToCategories()
            })
        DividerTextAndSpace(R.string.select_region)

        TextFieldUnEditable(productTitle = region?.title,
            modifier = Modifier.fillMaxWidth(),
            title = R.string.please_select_region,
            isFocusedOrClicked = {
                navigateToSelectRegions()
            })
        DividerTextAndSpace(R.string.enter_amount)

        if (state.regions != null) {
            Row(
                modifier = Modifier.wrapContentHeight(), verticalAlignment = Alignment.Bottom
            ) {
                currencyItem = state.regions[0]
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    SpinnerSample(
                        list = state.regions, preselected = state.regions[0], onSelectionChanged = {
                            descriptionFocusRequester.requestFocus()
                            currencyItem = it
                        }, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen12Dp))
                TextFieldForProduct(
                    productState = productPriceState,
                    onImeAction = {
                        keyboardController?.hide()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2f)
                        .focusRequester(descriptionFocusRequester),
                    title = R.string.price_5000,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            }
        }


        DividerTextAndSpace(R.string.select_image_for_your_product)

        if (capturedImageUri.path?.isNotEmpty() == true) {
            images = listOf(capturedImageUri).map { it.toImageUrl(isFromCamera = true) }
        }

        ShowSelectedImages(imagesList = images, onAddButtonClicked = {
            showGalleryOrCameraDialog = true
        })

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen16Dp))

        val categoryIsAdded = !item?.title.isNullOrEmpty()
        val regionIsAdded = !region?.title.isNullOrEmpty()

        val isEnabled =
            productTitleState.isValid && productDescriptionState.isValid && categoryIsAdded && regionIsAdded && productPriceState.isValid && currencyItem.id != -1 && (images.isNotEmpty() && images.size < 10)

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

            submitProduct(
                productTitleState.text,
                productDescriptionState.text,
                productPriceState.text,
                currencyItem.id,
                region!!.id,
                item!!.id,
                images
            )
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
                text = stringResource(id = R.string.continuee),
                style = MaterialTheme.typography.titleSmall
            )
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen24Dp))
        if (showGalleryOrCameraDialog) {
            DialogCameraOrGallery(onDismiss = {
                showGalleryOrCameraDialog = false
            }, onCameraSelected = {
                showGalleryOrCameraDialog = false
                RunTimePermission().permissionListForCamera(
                    cameraPermission = {
                        if (it) {
                            cameraLauncher.launch(uri)
                        }
                    }, context
                )
            }, onGallerySelected = {
                showGalleryOrCameraDialog = false
                RunTimePermission().permissionForGallery(
                    galleryPermission = {
                        if (it) {
                            galleryLauncher.launch("image/*")
                        }
                    }, context
                )
            })
        }

    }
}


@Composable
fun DividerTextAndSpace(@StringRes title: Int) {
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
    ProductTitle(title = title)
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen2Dp))

}

fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}