package org.don.onlineTrade.ui.main.home.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.don.onlineTrade.data.local.SearchHistoryDao
import org.don.onlineTrade.data.local.SearchHistoryEntity
import org.don.onlineTrade.data.remote.models.searchSuggestion.CategorySuggestion
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.SearchSuggestionsUseCase
import org.don.onlineTrade.utils.SharedPref
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchSuggestionsUseCase: SearchSuggestionsUseCase,
    private val searchHistoryDao: SearchHistoryDao
) : ViewModel() {

    var suggestions by mutableStateOf<List<CategorySuggestion>>(emptyList())
        private set

    var hasFetchedSuggestions by mutableStateOf(false)
        private set

    var isLoadingSuggestions by mutableStateOf(false)
        private set

    var queryText by mutableStateOf("")
    var searchLat by mutableStateOf(SharedPref.latitude.toDoubleOrNull() ?: 0.0)
        private set
    var searchLon by mutableStateOf(SharedPref.longitude.toDoubleOrNull() ?: 0.0)
        private set
    var searchRadiusKm by mutableIntStateOf(SharedPref.radius)
        private set

    // Search history
    val searchHistory = searchHistoryDao.getRecentHistory()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    var isTyping by mutableStateOf(false)
        private set

    private val _suggestionQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _suggestionQuery
                .debounce(300)
                .collectLatest { query ->
                    if (query.isNotEmpty()) {
                        fetchSuggestionsInternal(query)
                    }
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
            radius = searchRadiusKm,
            lang = SharedPref.language
        ).collect { result ->
            when (result) {
                is Resource.Success -> {
                    queryText = result.data?.queryText ?: ""
                    suggestions = result.data?.categories ?: emptyList()
                    hasFetchedSuggestions = true
                    isLoadingSuggestions = false
                }
                is Resource.Error -> {
                    suggestions = emptyList()
                    hasFetchedSuggestions = true
                    isLoadingSuggestions = false
                }
                is Resource.Loading -> {
                    isLoadingSuggestions = true
                }
            }
        }
    }

    fun saveToHistory(query: String, categoryName: String?, categoryId: Long?) {
        if (query.isBlank()) return
        viewModelScope.launch {
            searchHistoryDao.upsert(
                SearchHistoryEntity(
                    query = query,
                    categoryName = categoryName,
                    categoryId = categoryId
                )
            )
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchHistoryDao.clearAll()
        }
    }

    fun updateSearchLocation(lat: Double, lon: Double, radiusKm: Int) {
        searchLat = lat
        searchLon = lon
        searchRadiusKm = radiusKm
    }

    fun onSuggestionQueryChanged(query: String) {
        isTyping = query.isNotEmpty()
        _suggestionQuery.value = query
        if (query.isEmpty()) {
            suggestions = emptyList()
        }
    }

    fun clearSuggestions() {
        suggestions = emptyList()
        isTyping = false
    }
}
