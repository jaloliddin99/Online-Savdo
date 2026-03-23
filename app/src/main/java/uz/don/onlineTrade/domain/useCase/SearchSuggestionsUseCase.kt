package uz.don.onlineTrade.domain.useCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.don.onlineTrade.data.remote.models.searchSuggestion.CategorySuggestion
import uz.don.onlineTrade.data.remote.models.searchSuggestion.SearchSuggestionData
import uz.don.onlineTrade.domain.repository.NetworkRepository
import uz.don.onlineTrade.domain.state.Resource
import javax.inject.Inject

class SearchSuggestionsUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    operator fun invoke(
        query: String,
        lat: Double,
        lon: Double,
        radius: Int,
        lang: String
    ): Flow<Resource<SearchSuggestionData>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.getSearchSuggestions(query, lat, lon, radius, lang)
            emit(Resource.Success(response.data))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}
