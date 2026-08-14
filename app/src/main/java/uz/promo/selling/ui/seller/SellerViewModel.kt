package uz.promo.selling.ui.seller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import uz.promo.selling.data.paging.UserPostsPagingSource
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.data.remote.models.getProfile.SellerInfo
import uz.promo.selling.data.remote.models.getPublicProducts.Content
import javax.inject.Inject

@HiltViewModel
class SellerViewModel @Inject constructor(
    private val apiInterface: ApiInterface,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: Int = savedStateHandle.get<Int>("userId") ?: 0

    var seller by mutableStateOf<SellerInfo?>(null)
        private set

    init {
        viewModelScope.launch {
            try {
                val res = apiInterface.getPublicUser(userId)
                if (res.success) seller = res.data
            } catch (_: Exception) {
            }
        }
    }

    val products: Flow<PagingData<Content>> = Pager(
        // Offset-based backend: initialLoadSize must equal pageSize so page offsets
        // stay consistent and don't cause runaway appends. See HomeViewModel.
        config = PagingConfig(pageSize = 20, initialLoadSize = 20, enablePlaceholders = false),
        pagingSourceFactory = {
            UserPostsPagingSource(apiInterface, userId = userId)
        }
    ).flow.cachedIn(viewModelScope)
}
