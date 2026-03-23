package uz.don.onlineTrade.domain.useCase.presentUseCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.don.onlineTrade.data.remote.models.ModelSuccess
import uz.don.onlineTrade.domain.repository.NetworkRepository
import uz.don.onlineTrade.domain.state.Resource
import java.io.IOException
import javax.inject.Inject

class DeletePostUseCase@Inject constructor(
    private val repository: NetworkRepository
) {
    operator fun invoke(
        id: Int,
        token: String,
    ): Flow<Resource<ModelSuccess>> = flow {
        try {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    repository.deletePost(
                        id,
                        token,
                    )
                )
            )
        } catch(e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }


}