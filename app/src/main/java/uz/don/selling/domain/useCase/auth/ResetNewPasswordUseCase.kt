package uz.don.selling.domain.useCase.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.don.selling.data.remote.models.ModelSuccess
import uz.don.selling.domain.repository.NetworkRepository
import uz.don.selling.domain.state.Resource
import java.io.IOException
import javax.inject.Inject

class ResetNewPasswordUseCase @Inject constructor(
    private val repository: NetworkRepository
) {

    operator fun invoke(
        email: String,
        code: Int,
        password: String
    ): Flow<Resource<ModelSuccess>> = flow {
        try {
            emit(Resource.Loading())
            val data = repository.resetNewPassword(email, code, password)
            if (data.success){
                emit(Resource.Success(data))
            }else{
                emit(Resource.Error(data.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }


}