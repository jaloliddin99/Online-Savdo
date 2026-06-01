package uz.promo.selling.domain.useCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.promo.selling.data.remote.models.searchSuggestion.SearchSuggestionData
import uz.promo.selling.domain.repository.NetworkRepository
import uz.promo.selling.domain.state.Resource
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
