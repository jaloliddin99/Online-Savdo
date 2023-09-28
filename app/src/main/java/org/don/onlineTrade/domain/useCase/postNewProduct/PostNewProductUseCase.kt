package org.don.onlineTrade.domain.useCase.postNewProduct

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.RequestBody
import org.don.onlineTrade.data.remote.models.RegisterMain
import org.don.onlineTrade.data.remote.models.post.PostModel
import org.don.onlineTrade.domain.repository.NetworkRepository
import org.don.onlineTrade.domain.state.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PostNewProductUseCase@Inject constructor(
    private val repository: NetworkRepository
) {


    operator fun invoke(
        email: String,
        requestBody: RequestBody,
    ): Flow<Resource<PostModel>> = flow {
        try {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    repository.newProduct(
                        email,
                        requestBody,
                    )
                )
            )
        } catch(e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }


}