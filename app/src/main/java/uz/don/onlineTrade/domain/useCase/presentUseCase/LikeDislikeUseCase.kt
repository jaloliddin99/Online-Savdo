package uz.don.onlineTrade.domain.useCase.presentUseCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.don.onlineTrade.data.remote.models.GenericModel
import uz.don.onlineTrade.data.remote.models.showProducts.PostDetailsData
import uz.don.onlineTrade.domain.repository.NetworkRepository
import uz.don.onlineTrade.domain.state.Resource
import java.io.IOException
import javax.inject.Inject

class LikeDislikeUseCase@Inject constructor(
    private val repository: NetworkRepository
) {

    operator fun invoke(
        id: Int,
        token: String,
        language: String
    ): Flow<Resource<GenericModel<PostDetailsData>>> = flow {
        try {
            emit(Resource.Loading())


            val data = repository.likePost(  id, token, language)
            if (data.success){
                emit(Resource.Success(data))
            }else{
                emit(Resource.Error(data.message))
            }
        } catch(e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }


}