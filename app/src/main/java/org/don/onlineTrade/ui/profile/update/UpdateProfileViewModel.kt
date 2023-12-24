package org.don.onlineTrade.ui.profile.update

import android.app.Application
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
import org.don.onlineTrade.data.remote.models.ModelSuccess
import org.don.onlineTrade.data.remote.models.getProfile.UpdateProfileModel
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.UpdateProfileUseCase
import org.don.onlineTrade.ui.add.ImageUrl
import org.don.onlineTrade.ui.add.getRealPathFromURI
import org.don.onlineTrade.ui.add.sizeInKb
import org.don.onlineTrade.ui.home.AddProductScreenState
import org.don.onlineTrade.ui.home.GetProfileState
import org.don.onlineTrade.ui.home.UpdateProfileState
import org.don.onlineTrade.utils.FileManager.getFileFromUri
import org.don.onlineTrade.utils.SharedPref
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UpdateProfileViewModel @Inject constructor(
    private val updateProfileUseCase: UpdateProfileUseCase
):ViewModel() {




    private val _state = mutableStateOf(UpdateProfileState())
    val state: State<UpdateProfileState> = _state

    fun updateProfile(
        token: String = SharedPref.deviceToken,
        body: UpdateProfileModel
    ) {
        updateProfileUseCase(
            token,
            body
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = UpdateProfileState(getProfile = result.data)
                }

                is Resource.Error -> {
                    _state.value = UpdateProfileState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }

                is Resource.Loading -> {
                    _state.value = UpdateProfileState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }


}