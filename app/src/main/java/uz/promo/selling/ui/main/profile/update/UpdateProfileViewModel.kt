package uz.promo.selling.ui.main.profile.update

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.promo.selling.data.remote.models.getProfile.UpdateProfileModel
import uz.promo.selling.domain.state.Resource
import uz.promo.selling.domain.useCase.UpdateProfileUseCase
import uz.promo.selling.ui.main.home.UpdateProfileState
import uz.promo.selling.utils.SharedPref
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