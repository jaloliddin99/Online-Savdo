package uz.don.selling.ui.main.saved

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.don.selling.data.remote.models.getPublicProducts.Content
import uz.don.selling.domain.useCase.GetLikedProductUseCase
import uz.don.selling.utils.pagerForSaved.DefaultPaginator
import javax.inject.Inject

@HiltViewModel
class LikedViewModel @Inject constructor(private val getLikedProductUseCase: GetLikedProductUseCase) :
    ViewModel() {


    var pagerState by mutableStateOf(ScreenState())

    private var paginator = DefaultPaginator(
        initialKey = pagerState.page,
        onLoadUpdated = {
            pagerState = pagerState.copy(isLoading = it)
        },
        onRequest = { nextPage->

            getLikedProductUseCase.getItems(
                page = nextPage,
                pageSize = 20,
            )
        },
        getNextKey = {
            pagerState.page + 1
        },
        onError = {
            pagerState = pagerState.copy(error = it?.localizedMessage)
        },
        onSuccess = { items, newKey ->

            pagerState = pagerState.copy(
                items = pagerState.items + items,
                page = newKey,
                endReached = items.isEmpty()
            )
        }
    )


    fun loadNextItems() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    data class ScreenState(
        val isLoading: Boolean = false,
        val items: List<Content> = emptyList(),
        val error: String? = null,
        val endReached: Boolean = false,
        val page: Int = 0
    )


}