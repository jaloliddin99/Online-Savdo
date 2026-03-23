package uz.don.onlineTrade.ui.filterCategory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import uz.don.onlineTrade.data.paging.ProductsPagingSource
import uz.don.onlineTrade.data.remote.ApiInterface
import uz.don.onlineTrade.data.remote.models.getPublicProducts.Content
import javax.inject.Inject

@HiltViewModel
class FilterCategoryViewModel @Inject constructor(
    private val apiInterface: ApiInterface,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryId: Int? = savedStateHandle.get<Int>("param")

    val products: Flow<PagingData<Content>> = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = {
            ProductsPagingSource(apiInterface, categoryId = categoryId)
        }
    ).flow.cachedIn(viewModelScope)
}
