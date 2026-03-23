package uz.don.selling.domain.useCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.don.selling.data.remote.models.ModelSuccess
import uz.don.selling.data.remote.models.getProfile.UpdatePasswordModel
import uz.don.selling.domain.repository.NetworkRepository
import uz.don.selling.domain.state.Resource
import java.io.IOException
import javax.inject.Inject

class UpdatePasswordUseCase @Inject constructor(
    private val repository: NetworkRepository
) {

    operator fun invoke(
        token: String,
        body: UpdatePasswordModel
    ): Flow<Resource<ModelSuccess>> = flow {
        try {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    repository.updatePassword(
                        token,
                        body
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