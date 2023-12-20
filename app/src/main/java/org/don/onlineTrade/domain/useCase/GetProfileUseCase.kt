package org.don.onlineTrade.domain.useCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.don.onlineTrade.data.remote.models.getProfile.ModelGetProfile
import org.don.onlineTrade.domain.repository.NetworkRepository
import org.don.onlineTrade.domain.state.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: NetworkRepository
) {


    operator fun invoke(
        token: String
    ): Flow<Resource<ModelGetProfile>> = flow {
        try {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    repository.getProfile(
                        token
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