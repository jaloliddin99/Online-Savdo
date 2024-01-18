package org.don.onlineTrade.ui.add

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.zelory.compressor.Compressor
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.category.CategoryItem
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.CategoryMainUseCase
import org.don.onlineTrade.domain.useCase.LocationReverseUseCase
import org.don.onlineTrade.domain.useCase.postNewProduct.PostNewProductUseCase
import org.don.onlineTrade.ui.MapScreenData
import org.don.onlineTrade.ui.auth.TextFieldState
import org.don.onlineTrade.ui.home.AddProductScreenState
import org.don.onlineTrade.utils.FileManager.getFileFromUri
import org.don.onlineTrade.utils.LOCATION_REVERSE_URL
import org.don.onlineTrade.utils.SharedPref
import java.io.File
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class AddProductScreenViewModel @Inject constructor(
    private val postNewProductUseCase: PostNewProductUseCase,
    private val application: Application,
    private val categoryMainUseCase: CategoryMainUseCase,
) : AndroidViewModel(application) {


    var categoryValue: CategoryItem by mutableStateOf(CategoryItem())

    fun categoryValue(newValue: CategoryItem) {
        categoryValue = newValue
    }

    var mapValue: MapScreenData by mutableStateOf(MapScreenData())

    fun mapScreenValue(mapScreenData: MapScreenData){
        mapValue = mapScreenData
    }

    var titleValue: TextFieldState by mutableStateOf(ProductTitleState())
    fun setTitle(newValue: TextFieldState) {
        titleValue = newValue
    }

    var descriptionVM: TextFieldState by mutableStateOf(ProductDescriptionState())
    fun setDescription(newValue: TextFieldState) {
        descriptionVM = newValue
    }

    private var _imageList = mutableStateListOf<ImageUrl>()
    val imageList: List<ImageUrl> get() = _imageList
    fun setImageList(newItem: List<ImageUrl>) {
        viewModelScope.launch {
            _imageList = newItem.toMutableStateList()
        }
    }


    private fun clearStoredValues() {
        categoryValue(CategoryItem())
        setTitle(ProductTitleState())
        setDescription(ProductDescriptionState())
        setImageList(listOf())
    }


    private val _state = mutableStateOf(AddProductScreenState())
    val state: State<AddProductScreenState> = _state


    fun getCategoryDerails(categoryId: Int) {
        categoryMainUseCase.invoke(
            token = SharedPref.deviceToken,
            categoryId = categoryId
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = AddProductScreenState(
                        categoryDetail = result.data,
                    )
                }

                is Resource.Error -> {
                    _state.value =
                        AddProductScreenState(
                            error = result.message ?: "An unexpected error occurred"
                        )
                }

                is Resource.Loading -> {
                    _state.value = AddProductScreenState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }


    fun postNewProduct(
        token: String = SharedPref.deviceToken,
        titleProduct: String,
        descriptionProduct: String,
        categoryId: Int,
        images: List<ImageUrl>,
    ) {
        val builder: MultipartBody.Builder =
            MultipartBody.Builder().setType(MultipartBody.FORM)
        builder.addFormDataPart("userId", SharedPref.userId.toString())
        builder.addFormDataPart("title", titleProduct)
        builder.addFormDataPart("description", descriptionProduct)
        builder.addFormDataPart("category_id", categoryId.toString())
//        builder.addFormDataPart("region_id", region.toString())
//        builder.addFormDataPart("district_id", districtId.toString())
//        builder.addFormDataPart("lat", lat?:"0.0")
//        builder.addFormDataPart("lon", lon?:"0.0")

        val contentResolver = application.contentResolver

        viewModelScope.launch {
            for (photoUri in images) {
                if (!photoUri.isFromCamera) {
                    getRealPathFromURI(photoUri.uri, application)?.let { photoPath ->
                        val file = File(photoPath)
                        val compressedImageFile = Compressor.compress(application, file)
                        if (compressedImageFile.sizeInKb > 1000) {
                            Toast.makeText(
                                application,
                                application.getString(R.string.selectet_image_size),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }
                        if (compressedImageFile.exists()) {
                            builder.addFormDataPart(
                                "files",
                                compressedImageFile.name,
                                RequestBody.create(
                                    "image/*".toMediaTypeOrNull(),
                                    compressedImageFile
                                )
                            )
                        }
                    }
                } else {
                    contentResolver.getFileFromUri(photoUri.uri, application)?.let { file ->
                        val compressedImageFile = Compressor.compress(application, file)
                        if (compressedImageFile.sizeInKb > 1000) {
                            Toast.makeText(
                                application,
                                application.getString(R.string.selectet_image_size),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }
                        if (compressedImageFile.exists()) {
                            builder.addFormDataPart(
                                "files",
                                compressedImageFile.name,
                                RequestBody.create(
                                    "image/*".toMediaTypeOrNull(),
                                    compressedImageFile
                                )
                            )
                        }
                    }

                }

            }
            val requestBody: RequestBody = builder.build()

            postNewProductUseCase(
                token,
                requestBody,
            ).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        clearStoredValues()
                        _state.value = AddProductScreenState(
                            postNewProduct = result.data,
                        )
                    }

                    is Resource.Error -> {
                        _state.value =
                            AddProductScreenState(
                                error = result.message ?: "An unexpected error occurred"
                            )
                    }

                    is Resource.Loading -> {
                        _state.value = AddProductScreenState(
                            isLoading = true,
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }


    }

    fun updateShowSuccessDialog(show: Boolean) {
        _state.value = _state.value.copy(showSuccessDialog = show)
    }

    fun updateSHowCameraOrGallery(show: Boolean) {
        _state.value = _state.value.copy(showCameraOrGalleryDialog = show)
    }

}

fun getRealPathFromURI(contentURI: Uri, application: Application): String? {
    val filePath: String?
    val cursor = application.contentResolver.query(contentURI, null, null, null, null)
    if (cursor == null) {
        filePath = contentURI.path
    } else {
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        filePath = cursor.getString(idx)
        cursor.close()
    }
    return filePath
}

val File.size get() = if (!exists()) 0.0 else length().toDouble()
val File.sizeInKb get() = size / 1024