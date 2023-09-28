package org.don.onlineTrade.ui.add

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.zelory.compressor.Compressor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.don.onlineTrade.R
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.currencies.CurrenciesUseCase
import org.don.onlineTrade.domain.useCase.postNewProduct.PostNewProductUseCase
import org.don.onlineTrade.ui.home.AddProductScreenState
import org.don.onlineTrade.ui.home.RegionsScreenState
import org.don.onlineTrade.ui.home.TOKEN
import org.don.onlineTrade.utils.SharedPref
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddProductScreenViewModel @Inject constructor(
    private val currencyUseCase: CurrenciesUseCase,
    private val postNewProductUseCase: PostNewProductUseCase,
    private val application: Application
): AndroidViewModel(application) {



    private val _state = mutableStateOf(AddProductScreenState())
    val state: State<AddProductScreenState> = _state

    init {
        getAllCategories(
            token = TOKEN,
            language = "uz"
        )
    }

    private fun getAllCategories(
        token: String,
        language: String,
    ) {
        currencyUseCase(
            token,
            language,
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = AddProductScreenState(regions = result.data)
                }

                is Resource.Error -> {
                    _state.value = AddProductScreenState(
                        error = result.message ?: "An unexpected error occured"
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
         priceText: String,
         currencyId: Int,
         region: Int,
         categoryId: Int,
         images: List<ImageUrl>,
         isPost: Boolean = false,
         productId: Int? = null,
    ) {

         val builder: MultipartBody.Builder =
             MultipartBody.Builder().setType(MultipartBody.FORM)
         builder.addFormDataPart("title", titleProduct)
         builder.addFormDataPart("description", descriptionProduct)
         builder.addFormDataPart("price", priceText)
         builder.addFormDataPart("category_id", categoryId.toString())
         builder.addFormDataPart("region_id", region.toString())
         builder.addFormDataPart("currency_id", currencyId.toString())
         viewModelScope.launch {
             for (photoUri in images) {
                 if (!photoUri.isFromCamera){
                     getRealPathFromURI(photoUri.uri)?.let { photoPath ->
                         val file = File(photoPath)
                         val compressedImageFile = Compressor.compress(application, file)
                         if (compressedImageFile.sizeInKb > 2000) {
                             Toast.makeText(
                                 application,
                                 application.getString(R.string.selectet_image_size),
                                 Toast.LENGTH_SHORT
                             ).show()
                             return@launch
                         }
                         if (compressedImageFile.exists()) {
                             builder.addFormDataPart(
                                 "images[]",
                                 compressedImageFile.name,
                                 RequestBody.create(
                                     "image/*".toMediaTypeOrNull(),
                                     compressedImageFile
                                 )
                             )
                         }
                     }
                 }else{
                     val file = File(photoUri.uri.toString())
                     val compressedImageFile = Compressor.compress(application, file)
                     if (compressedImageFile.sizeInKb > 2000) {
                         Toast.makeText(
                             application,
                             application.getString(R.string.selectet_image_size),
                             Toast.LENGTH_SHORT
                         ).show()
                         return@launch
                     }
                     if (compressedImageFile.exists()) {
                         builder.addFormDataPart(
                             "images[]",
                             compressedImageFile.name,
                             RequestBody.create(
                                 "image/*".toMediaTypeOrNull(),
                                 compressedImageFile
                             )
                         )
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
                         _state.value = AddProductScreenState(postNewProduct = result.data)
                     }

                     is Resource.Error -> {
                         _state.value = AddProductScreenState(
                             error = result.message ?: "An unexpected error occured"
                         )
                     }

                     is Resource.Loading -> {
                         _state.value = AddProductScreenState(isLoading = true)
                     }
                 }
             }.launchIn(viewModelScope)
         }




    }


    private fun getRealPathFromURI(contentURI: Uri): String? {
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
    private val File.sizeInKb get() = size / 1024
}