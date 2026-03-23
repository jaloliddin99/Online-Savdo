package uz.don.onlineTrade.ui.main.profile

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.zelory.compressor.Compressor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import uz.don.onlineTrade.R
import uz.don.onlineTrade.domain.state.Resource
import uz.don.onlineTrade.domain.useCase.GetProfileUseCase
import uz.don.onlineTrade.ui.main.add.ImageUrl
import uz.don.onlineTrade.ui.main.add.getRealPathFromURI
import uz.don.onlineTrade.ui.main.add.sizeInKb
import uz.don.onlineTrade.ui.main.home.GetProfileState
import uz.don.onlineTrade.utils.FileManager.getFileFromUri
import uz.don.onlineTrade.utils.SharedPref
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val application: Application
) : AndroidViewModel(application)  {



    private val _state = mutableStateOf(GetProfileState())
    val state: State<GetProfileState> = _state

    fun refresh(){
        getProfile(
            token = SharedPref.deviceToken,
        )
    }

    init {
        getProfile(
            token = SharedPref.deviceToken,
        )
    }

    private fun getProfile(
        token: String,
    ) {
        getProfileUseCase(
            token,
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    SharedPref.userId = result.data?.data?.id ?: -1
                    _state.value = GetProfileState(getProfile = result.data?.data)
                }

                is Resource.Error -> {
                    _state.value = GetProfileState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }

                is Resource.Loading -> {
                    _state.value = GetProfileState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }


    fun updateProfileImage(
        photoUri: ImageUrl,
    ){
        val contentResolver = application.contentResolver

        val builder: MultipartBody.Builder =
            MultipartBody.Builder().setType(MultipartBody.FORM)

        viewModelScope.launch {
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
                            "file",
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
                            "file",
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

            getProfileUseCase.updateProfileImage(
                token = SharedPref.deviceToken,
                requestBody,
            ).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        refresh()
                        _state.value = _state.value.copy(updateProfileImage = result.data)
                    }

                    is Resource.Error -> {
                        refresh()
                        _state.value = GetProfileState(
                            error = result.message ?: "An unexpected error occured"
                        )
                    }
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }

    }

}