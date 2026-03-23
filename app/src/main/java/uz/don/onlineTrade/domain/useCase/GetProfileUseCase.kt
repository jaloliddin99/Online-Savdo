package uz.don.onlineTrade.domain.useCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.RequestBody
import uz.don.onlineTrade.data.remote.models.GenericModel
import uz.don.onlineTrade.data.remote.models.ModelSuccess
import uz.don.onlineTrade.data.remote.models.getProfile.User
import uz.don.onlineTrade.domain.repository.NetworkRepository
import uz.don.onlineTrade.domain.state.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: NetworkRepository
) {

    operator fun invoke(
        token: String
    ): Flow<Resource<GenericModel<User>>> = flow {
        try {
            emit(Resource.Loading())

            val data = repository.getProfile(token)
            if (data.success) {
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error(data.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    fun updateProfileImage(
        token: String,
        body: RequestBody
    ): Flow<Resource<ModelSuccess>> = flow {
        try {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    repository.updateProfileImage(
                        token,
                        body
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