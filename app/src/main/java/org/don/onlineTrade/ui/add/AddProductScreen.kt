package org.don.onlineTrade.ui.add

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.graphics.drawable.toIcon
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.category.CompactedCategoryItem
import org.don.onlineTrade.data.remote.models.currencies.ModelCurrencyListsItem
import org.don.onlineTrade.data.remote.models.region.RegionDistrictModelItem
import org.don.onlineTrade.ui.dialogs.settings.SettingsDialog
import org.don.onlineTrade.ui.home.AddProductScreenState
import org.don.onlineTrade.ui.region.RegionsViewModel
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading
import org.don.onlineTrade.utils.runTimePermission.OnRunTimePermissionListener
import org.don.onlineTrade.utils.runTimePermission.RunTimePermission
import java.io.IOException


@Composable
fun AddProductRoute(
    navigateToCategories: () -> Unit,
    navigateToSelectRegions: () -> Unit,
    modifier: Modifier = Modifier,
    item: CompactedCategoryItem? = null,
    regions: RegionDistrictModelItem? = null,
) {

    val addProductViewModel = hiltViewModel<AddProductScreenViewModel>()
    val state = addProductViewModel.state.value

    AddProductScreen(
        modifier = modifier,
        navigateToCategories,
        navigateToSelectRegions,
        item,
        regions,
        state
    )
}

@Composable
fun AddProductScreen(
    modifier: Modifier,
    navigateToCategories: () -> Unit,
    navigateToSelectRegions: () -> Unit,
    item: CompactedCategoryItem? = null,
    region: RegionDistrictModelItem? = null,
    state: AddProductScreenState
) {


    var showGalleryOrCameraDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val productTitleState by rememberSaveable(stateSaver = ProductTitleStateSaver) {
        mutableStateOf(ProductTitleState())
    }

    val productDescriptionState by rememberSaveable(stateSaver = ProductDescriptionStateSaver) {
        mutableStateOf(ProductTitleState())
    }

    val categoryState by rememberSaveable(stateSaver = CategoryStateSaver) {
        mutableStateOf(ProductTitleState())
    }

    val regionState by rememberSaveable(stateSaver = RegionStateSaver) {
        mutableStateOf(ProductTitleState())
    }

    val productPriceState by rememberSaveable(stateSaver = ProductPriceStateSaver) {
        mutableStateOf(ProductTitleState())
    }

    var currencyItem by remember {
        mutableStateOf(ModelCurrencyListsItem())
    }

    val isFeedLoading = state.isLoading
    val context = LocalContext.current

    if (state.error.isNotBlank()) {
        Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
    }

    var images by remember { mutableStateOf(listOf<Uri>()) }
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            images = it
        }

    val focusRequester = remember { FocusRequester() }
    FreeLoading(isFeedLoading)
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = MaterialTheme.spacing.dimen16Dp
            ),
        horizontalAlignment = Alignment.Start,
    ) {

        item {
            ProductTitle(title = R.string.enter_title)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen2Dp))
            TextFieldForProduct(
                productState = productTitleState,
                onImeAction = {
                    focusRequester.requestFocus()
                },
                modifier = Modifier
                    .fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
            ProductTitle(title = R.string.add_description)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen2Dp))

            TextFieldForProduct(
                productState = productDescriptionState,
                onImeAction = {
                    focusRequester.requestFocus()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .height(200.dp),
                title = R.string.please_enter_description
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
            ProductTitle(title = R.string.select_category)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen2Dp))

            if (item != null) {
                categoryState.text = item.title
            }
            TextFieldUnEditable(
                productState = categoryState,
                modifier = Modifier.fillMaxWidth(),
                title = R.string.please_select_category,
                isFocusedOrClicked = {
                    navigateToCategories()
                }
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
            ProductTitle(title = R.string.select_region)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen2Dp))


            if (region != null) {
                regionState.text = region.title
            }
            TextFieldUnEditable(
                productState = regionState,
                modifier = Modifier.fillMaxWidth(),
                title = R.string.please_select_region,
                isFocusedOrClicked = {
                    navigateToSelectRegions()
                }
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
            ProductTitle(title = R.string.enter_amount)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen2Dp))


            if (state.regions != null) {
                Row(
                    modifier = Modifier.wrapContentHeight(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    SpinnerSample(
                        list = state.regions,
                        preselected = state.regions[0],
                        onSelectionChanged = {
                            focusRequester.requestFocus()
                            currencyItem = it
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen12Dp))
                    TextFieldForProduct(
                        productState = productPriceState,
                        onImeAction = {
                            //focusRequester.requestFocus()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(2f)
                            .focusRequester(focusRequester),
                        title = R.string.price_5000,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                }
            }


            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
            ProductTitle(title = R.string.select_image_for_your_product)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen2Dp))

            ShowSelectedImages(
                onAddButtonClicked = {
                showGalleryOrCameraDialog = true
            },
                imagesList = images.map { it.toImageUrl(false) }
                )

            if (showGalleryOrCameraDialog) {
                DialogCameraOrGallery(
                    onDismiss = {
                        showGalleryOrCameraDialog = false
                    },
                    onCameraSelected = {
                        showGalleryOrCameraDialog = false
                        RunTimePermission().permissionListForCamera(
                            cameraPermission = {
                                if (it) {
                                    galleryLauncher.launch("image/*")
                                }
                            }, context
                        )
                    },
                    onGallerySelected = {
                        showGalleryOrCameraDialog = false
                        RunTimePermission().permissionForGallery(
                            galleryPermission = {
                                if (it) {
                                    galleryLauncher.launch("image/*")
                                }
                            }, context
                        )
                    }
                )
            }
        }
    }
}


