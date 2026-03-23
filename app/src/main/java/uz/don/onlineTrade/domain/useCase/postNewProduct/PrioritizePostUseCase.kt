package uz.don.onlineTrade.domain.useCase.postNewProduct

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import uz.don.onlineTrade.data.remote.models.ModelSuccess
import uz.don.onlineTrade.data.remote.models.post.PostModel
import uz.don.onlineTrade.domain.repository.NetworkRepository
import uz.don.onlineTrade.domain.state.Resource
import java.io.IOException
import javax.inject.Inject

class PrioritizePostUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    operator fun invoke(
        token: String,
        postId: Long,
        period: Int
    ): Flow<Resource<ModelSuccess>> = flow {
        try {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    repository.prioritizePost(
                       token, postId, period
                    ),
                )
            )
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }


}