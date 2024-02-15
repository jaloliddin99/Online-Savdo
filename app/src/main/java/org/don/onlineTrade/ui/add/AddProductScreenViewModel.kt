package org.don.onlineTrade.ui.add

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import id.zelory.compressor.Compressor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.don.onlineTrade.data.remote.models.category.CategoryItem
import org.don.onlineTrade.data.remote.models.post.PostParamDTO
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.CategoryMainUseCase
import org.don.onlineTrade.domain.useCase.postNewProduct.PostNewProductUseCase
import org.don.onlineTrade.ui.auth.TextFieldState
import org.don.onlineTrade.ui.home.AddProductScreenState
import org.don.onlineTrade.ui.map.MapScreenData
import org.don.onlineTrade.utils.FileManager.getFileFromUri
import org.don.onlineTrade.utils.SharedPref
import java.io.File
import javax.inject.Inject


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
        mapData: MapScreenData,
        postParams: List<PostParamDTO>
    ) {
        val contentResolver = application.contentResolver
        val compressedList = ArrayList<File>()
        viewModelScope.launch {
            for (photoUri in images) {
                if (!photoUri.isFromCamera) {
                    getRealPathFromURI(photoUri.uri, application)?.let { photoPath ->
                        val file = File(photoPath)
                        val compressedImageFile = Compressor.compress(application, file)
                        if (compressedImageFile.exists()) {
                            compressedList.add(compressedImageFile)
                        }
                    }
                } else {
                    contentResolver.getFileFromUri(photoUri.uri, application)?.let { file ->
                        val compressedImageFile = Compressor.compress(application, file)
                        if (compressedImageFile.exists()) {
                            compressedList.add(compressedImageFile)
                        }
                    }
                }
            }
            val fileParts: List<MultipartBody.Part> = convertFilesToMultipart(compressedList)
            val postParamsRequestBody = createPostParamsRequestBody(postParams)

            postNewProductUseCase(
                token,
                titleProduct,
                descriptionProduct,
                categoryId.toLong(),
                mapData.regionId,
                mapData.districtId,
                mapData.lat ?: 0.0,
                mapData.lon ?: 0.0,
                mapData.addressName,
                mapData.addressDescription,
                SharedPref.userId,
                fileParts,
                postParamsRequestBody
            ).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        clearStoredValues()
                        _state.value = AddProductScreenState(
                            postNewProduct = result.data,
                            showSuccessDialog = true
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

    private fun convertFilesToMultipart(files: List<File>): List<MultipartBody.Part> {
        val fileParts: MutableList<MultipartBody.Part> = mutableListOf()

        for (file in files) {
            val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val filePart = MultipartBody.Part.createFormData("files", file.name, requestBody)
            fileParts.add(filePart)
        }

        return fileParts
    }

    private fun createPostParamsRequestBody(postParams: List<PostParamDTO>): RequestBody {
        val json = Gson().toJson(postParams)
        return RequestBody.create("application/json".toMediaTypeOrNull(), json)
    }

//    private fun createAddressParamRequestBody(postParams: MapScreenData): RequestBody {
//        val json = Gson().toJson(postParams)
//        return json.toRequestBody("application/json".toMediaTypeOrNull())
//    }


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