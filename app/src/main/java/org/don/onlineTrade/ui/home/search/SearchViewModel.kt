package org.don.onlineTrade.ui.home.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.don.onlineTrade.data.remote.models.searchSuggestion.CategorySuggestion
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.SearchSuggestionsUseCase
import org.don.onlineTrade.utils.SharedPref
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchSuggestionsUseCase: SearchSuggestionsUseCase
) : ViewModel() {

    var suggestions by mutableStateOf<List<CategorySuggestion>>(emptyList())
        private set

    var queryText by mutableStateOf("")
    var searchLat by mutableStateOf(SharedPref.latitude.toDoubleOrNull() ?: 0.0)
        private set
    var searchLon by mutableStateOf(SharedPref.longitude.toDoubleOrNull() ?: 0.0)
        private set
    var searchRadiusKm by mutableIntStateOf(SharedPref.radius)
        private set

    private val _suggestionQuery = MutableStateFlow("")

    init {
        fetchSuggestions("")
        viewModelScope.launch {
            _suggestionQuery
                .debounce(1000)
                .collectLatest { query ->
                    fetchSuggestionsInternal(query)
                }
        }
    }

    fun fetchSuggestions(query: String) {
        viewModelScope.launch {
            fetchSuggestionsInternal(query)
        }
    }

    private suspend fun fetchSuggestionsInternal(query: String) {
        searchSuggestionsUseCase(
            query = query,
            lat = searchLat,
            lon = searchLon,
            radius = searchRadiusKm.coerceAtLeast(10),
            lang = SharedPref.language
        ).collect { result ->
            when (result) {
                is Resource.Success -> {
                    queryText = result.data?.queryText ?: ""
                    suggestions = result.data?.categories ?: emptyList()
                }
                is Resource.Error -> {
                    suggestions = emptyList()
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun updateSearchLocation(lat: Double, lon: Double, radiusKm: Int) {
        searchLat = lat
        searchLon = lon
        searchRadiusKm = radiusKm
    }

    fun onSuggestionQueryChanged(query: String) {
        _suggestionQuery.value = query
    }

    fun clearSuggestions() {
        suggestions = emptyList()
    }
}
