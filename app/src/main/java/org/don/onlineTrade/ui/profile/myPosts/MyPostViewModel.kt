package org.don.onlineTrade.ui.profile.myPosts

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.postNewProduct.PrioritizePostUseCase
import org.don.onlineTrade.ui.home.MyProfileScreen
import org.don.onlineTrade.utils.SharedPref
import javax.inject.Inject

@HiltViewModel
class MyPostViewModel @Inject constructor(
    private val prioritizePostUseCase: PrioritizePostUseCase
): ViewModel() {


    private val _state = mutableStateOf(MyProfileScreen())
    val state: State<MyProfileScreen> = _state


    fun updateValues(
        token: String = SharedPref.deviceToken,
        postId: Long,
        period: Int
    ) {
        prioritizePostUseCase(
            token, postId, period
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = MyProfileScreen(getProfile = result.data)
                }

                is Resource.Error -> {
                    _state.value = MyProfileScreen(
                        error = result.message ?: "An unexpected error occured"
                    )
                }

                is Resource.Loading -> {
                    _state.value = MyProfileScreen(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }


}