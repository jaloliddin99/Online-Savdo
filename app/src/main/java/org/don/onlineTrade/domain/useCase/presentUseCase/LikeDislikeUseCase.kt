package org.don.onlineTrade.domain.useCase.presentUseCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.don.onlineTrade.data.remote.models.showProducts.PostDetailsModel
import org.don.onlineTrade.domain.repository.NetworkRepository
import org.don.onlineTrade.domain.state.Resource
import java.io.IOException
import javax.inject.Inject

class LikeDislikeUseCase@Inject constructor(
    private val repository: NetworkRepository
) {

    operator fun invoke(
        id: Int,
        token: String,
        language: String
    ): Flow<Resource<PostDetailsModel>> = flow {
        try {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    repository.likePost(
                        id,
                        token,
                        language
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