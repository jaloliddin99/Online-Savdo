package uz.don.selling.ui.detailsPage

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.don.selling.data.paging.ProductsPagingSource
import uz.don.selling.data.remote.ApiInterface
import uz.don.selling.data.remote.models.getPublicProducts.Content
import uz.don.selling.domain.state.Resource
import uz.don.selling.domain.useCase.presentUseCase.DeletePostUseCase
import uz.don.selling.domain.useCase.presentUseCase.LikeDislikeUseCase
import uz.don.selling.domain.useCase.presentUseCase.PresentProductUseCase
import uz.don.selling.ui.main.home.PresentProductState
import uz.don.selling.utils.SharedPref
import javax.inject.Inject

@HiltViewModel
class PresentViewModel @Inject constructor(
    private val presentProductUseCase: PresentProductUseCase,
    private val likeDislikeUseCase: LikeDislikeUseCase,
    private val deleteProductUseCase: DeletePostUseCase,
    private val apiInterface: ApiInterface,
): ViewModel() {

    val similarProducts: Flow<PagingData<Content>> = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { ProductsPagingSource(apiInterface) }
    ).flow.cachedIn(viewModelScope)

    private val _state = mutableStateOf(PresentProductState())
    val state: State<PresentProductState> = _state

    fun getProductDetail(
        id: Int,
        language: String
    ) {
        presentProductUseCase(
            id,
            language
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = PresentProductState(registerMain = result.data?.data)
                }

                is Resource.Error -> {
                    _state.value = PresentProductState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }

                is Resource.Loading -> {
                    _state.value = PresentProductState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun likePost(it: Int) {
        likeDislikeUseCase(
            id = it,
            language = SharedPref.language,
            token = SharedPref.deviceToken
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = PresentProductState(registerMain = result.data?.data)
                }

                is Resource.Error -> {
                    _state.value = PresentProductState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }

                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }


    fun deletePost(it: Int) {
        deleteProductUseCase(
            id = it,
            token = SharedPref.deviceToken
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = PresentProductState(delete = result.data)
                }

                is Resource.Error -> {
                    _state.value = PresentProductState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }

                is Resource.Loading -> {
                    _state.value = PresentProductState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }


}