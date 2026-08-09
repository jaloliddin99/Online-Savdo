package uz.promo.selling.ui.main.add

import android.app.Application
import android.graphics.Bitmap
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
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.ai.PriceSuggestionDTO
import uz.promo.selling.data.remote.models.post.PostParamDTO
import uz.promo.selling.domain.state.Resource
import uz.promo.selling.domain.useCase.CategoryMainUseCase
import uz.promo.selling.domain.useCase.ai.AiListingDraftUseCase
import uz.promo.selling.domain.useCase.ai.AiPriceSuggestionUseCase
import uz.promo.selling.domain.useCase.postNewProduct.PostNewProductUseCase
import uz.promo.selling.ui.auth.TextFieldState
import uz.promo.selling.ui.main.add.dynamic.DynamicViewData
import uz.promo.selling.ui.main.home.AddProductScreenState
import uz.promo.selling.ui.map.MapScreenData
import uz.promo.selling.utils.FileManager.getFileFromUri
import uz.promo.selling.utils.SharedPref
import java.io.File
import javax.inject.Inject


@HiltViewModel
class AddProductScreenViewModel @Inject constructor(
    private val postNewProductUseCase: PostNewProductUseCase,
    private val application: Application,
    private val categoryMainUseCase: CategoryMainUseCase,
    private val aiListingDraftUseCase: AiListingDraftUseCase,
    private val aiPriceSuggestionUseCase: AiPriceSuggestionUseCase,
) : AndroidViewModel(application) {


    var currentStep by mutableStateOf(1)
        private set

    fun goToNextStep() {
        if (currentStep < 3) currentStep++
    }

    fun goToPreviousStep() {
        if (currentStep > 1) currentStep--
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

    // Dynamic category-field values (AI pre-fills + user edits). Held in the VM
    // so navigating away mid-flow (map / category picker) doesn't wipe them —
    // the composables' own state dies with their composition.
    var dynamicViewData by mutableStateOf(mapOf<String, DynamicViewData>())
    // The category detail id dynamicViewData was built for; a different id means
    // the fields must be rebuilt for the new category.
    var dynamicViewDataCategoryId by mutableStateOf<Int?>(null)


    private fun clearStoredValues() {
        currentStep = 1
        setTitle(ProductTitleState())
        setDescription(ProductDescriptionState())
        setImageList(listOf())
        dynamicViewData = emptyMap()
        dynamicViewDataCategoryId = null
    }


    private val _state = mutableStateOf(AddProductScreenState())
    val state: State<AddProductScreenState> = _state


    fun getCategoryDerails(categoryId: Int) {
        categoryMainUseCase.invoke(
            categoryId = categoryId
        ).onEach { result ->
            when (result) {
                // copy() (not a fresh state) so the AI draft / loading flags survive
                // loading a category's detail in the AI review flow.
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        categoryDetail = result.data,
                        isLoading = false
                    )
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message ?: "An unexpected error occurred",
                        isLoading = false
                    )
                }

                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
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
        // Same instant-feedback + double-tap guard as generateDraftFromImages:
        // compression runs before the network call, so without this the button
        // looks unresponsive and repeated taps create duplicate posts.
        if (_state.value.isLoading) return
        _state.value = _state.value.copy(isLoading = true, error = "")
        viewModelScope.launch {
            val fileParts: List<MultipartBody.Part> = compressImagesToParts(images)
            if (fileParts.isEmpty()) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = application.getString(R.string.couldnt_read_images)
                )
                return@launch
            }
            val postParamsRequestBody = createPostParamsRequestBody(postParams)

            postNewProductUseCase(
                token,
                titleProduct,
                descriptionProduct,
                categoryId.toLong(),
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
                        _state.value = _state.value.copy(
                            isLoading = false,
                            postNewProduct = result.data,
                            showSuccessDialog = true
                        )
                    }

                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = result.message ?: "An unexpected error occurred"
                        )
                    }

                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, error = "")
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


    /**
     * Sends the picked photos to the AI, then pre-fills title + description and
     * stores the suggested post params (the screen merges those into the dynamic
     * fields by code). Runs off the UI thread; never blocks anything.
     */
    fun generateDraftFromImages(
        images: List<ImageUrl>,
        token: String = SharedPref.deviceToken,
        lang: String = SharedPref.language.ifBlank { "uz" },
        onComplete: (Boolean) -> Unit = {}
    ) {
        if (images.isEmpty()) return
        // Flip the loading state BEFORE the (slow) image compression so the
        // button shows progress instantly, and ignore taps while a generation
        // is already running — otherwise impatient re-taps start duplicate jobs.
        if (_state.value.isAiLoading) return
        _state.value = _state.value.copy(isAiLoading = true, error = "")
        viewModelScope.launch {
            val fileParts = compressImagesToParts(images)
            // Every picked image failed to read — bail out with a clear message
            // instead of letting OkHttp throw "Multipart body must have at least
            // one part."
            if (fileParts.isEmpty()) {
                _state.value = _state.value.copy(
                    isAiLoading = false,
                    error = application.getString(R.string.couldnt_read_images)
                )
                onComplete(false)
                return@launch
            }
            aiListingDraftUseCase(token, fileParts, lang).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isAiLoading = true, error = "")
                    }

                    is Resource.Success -> {
                        val envelope = result.data
                        val response = envelope?.data
                        // Backend returns HTTP 200 with success=false for quota/disabled.
                        if (envelope?.success == true && response?.draft != null) {
                            response.draft.title?.let { titleValue.text = it }
                            response.draft.description?.let { descriptionVM.text = it }
                            _state.value = _state.value.copy(isAiLoading = false, aiDraft = response)
                            onComplete(true)
                        } else {
                            _state.value = _state.value.copy(
                                isAiLoading = false,
                                error = envelope?.message ?: "AI couldn't generate a draft."
                            )
                            onComplete(false)
                        }
                    }

                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            isAiLoading = false,
                            error = result.message ?: "An unexpected error occurred"
                        )
                        onComplete(false)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    /**
     * Reads each picked image via the content resolver (works for gallery + camera
     * URIs on scoped storage), then compresses it. A file that can't be read or
     * compressed is skipped / sent uncompressed instead of crashing the app.
     */
    private suspend fun compressImagesToParts(images: List<ImageUrl>): List<MultipartBody.Part> {
        val contentResolver = application.contentResolver
        val readyFiles = ArrayList<File>()
        for (photoUri in images) {
            val file = contentResolver.getFileFromUri(photoUri.uri, application) ?: continue
            val ready = try {
                Compressor.compress(application, file) {
                    resolution(1600, 1600)
                    quality(80)
                    format(Bitmap.CompressFormat.JPEG)
                    // Hard cap so no image ever goes up near-raw.
                    size(700_000)
                }.takeIf { it.exists() } ?: file
            } catch (e: Exception) {
                // Compressor can throw if the file isn't a decodable image — send the raw copy.
                file
            }
            if (ready.exists()) {
                readyFiles.add(ready)
                // The raw copy is no longer needed once a compressed version exists.
                if (ready != file) file.delete()
            }
        }
        return convertFilesToMultipart(readyFiles)
    }

    /**
     * Fetches a SQL-percentile price suggestion for the category (no AI cost).
     * Delivers the DTO on success (envelope.success && data != null), or null on
     * any error / disabled response so the caller can show a fallback message.
     */
    fun suggestPrice(
        categoryId: Long,
        currency: String?,
        onResult: (PriceSuggestionDTO?) -> Unit
    ) {
        viewModelScope.launch {
            aiPriceSuggestionUseCase(categoryId, currency).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        val envelope = result.data
                        if (envelope?.success == true && envelope.data != null) {
                            onResult(envelope.data)
                        } else {
                            onResult(null)
                        }
                    }

                    is Resource.Error -> onResult(null)

                    is Resource.Loading -> { /* local loading handled in the UI */ }
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