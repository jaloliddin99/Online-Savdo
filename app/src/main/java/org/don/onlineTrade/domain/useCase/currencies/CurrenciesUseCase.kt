package org.don.onlineTrade.domain.useCase.currencies

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.don.onlineTrade.data.remote.models.currencies.ModelCurrencyListsItem
import org.don.onlineTrade.domain.repository.NetworkRepository
import org.don.onlineTrade.domain.state.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CurrenciesUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    operator fun invoke(
        token: String,
        language: String
    ): Flow<Resource<List<ModelCurrencyListsItem>>> = flow {
        try {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    repository.getAllCurrencies(
                        token,
                        language,
                    )
                )
            )
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}