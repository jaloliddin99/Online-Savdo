package uz.promo.selling.domain.useCase.ai

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.promo.selling.data.remote.models.GenericModel
import uz.promo.selling.data.remote.models.ai.PriceSuggestionDTO
import uz.promo.selling.domain.repository.NetworkRepository
import uz.promo.selling.domain.state.Resource
import uz.promo.selling.utils.SharedPref
import java.io.IOException
import javax.inject.Inject

class AiPriceSuggestionUseCase @Inject constructor(
    private val repository: NetworkRepository
) {

    operator fun invoke(
        categoryId: Long,
        currency: String?,
        token: String = SharedPref.deviceToken,
        lang: String = SharedPref.language
    ): Flow<Resource<GenericModel<PriceSuggestionDTO>>> = flow {
        try {
            emit(Resource.Loading())
            emit(Resource.Success(repository.aiPriceSuggestion(token, categoryId, currency, lang)))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        }
    }
}
